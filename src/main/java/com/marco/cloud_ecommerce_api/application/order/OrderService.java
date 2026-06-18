package com.marco.cloud_ecommerce_api.application.order;

import com.marco.cloud_ecommerce_api.domain.order.Order;
import com.marco.cloud_ecommerce_api.domain.order.OrderItem;
import com.marco.cloud_ecommerce_api.domain.order.OrderRepository;
import com.marco.cloud_ecommerce_api.domain.product.Product;
import com.marco.cloud_ecommerce_api.domain.product.ProductRepository;
import com.marco.cloud_ecommerce_api.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderDtoMapper orderDtoMapper;

    // --- MÉTODOS DE LECTURA ---

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Order with id" + id + " not found"));
        return orderDtoMapper.toResponseDTO(order);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findByIdempotentKey(String idempotentKey) {
        Order order = orderRepository.findByIdempotentKey(idempotentKey)
                .orElseThrow(()-> new RuntimeException("Order with" + idempotentKey + " not found"));
        return orderDtoMapper.toResponseDTO(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findByUserId(UUID userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderDtoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean canBePaid(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Order with id" + id + " not found"));
        return order.canBePaid();
    }

    @Transactional(readOnly = true)
    public boolean canBeCancelled(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Order with id" + id + " not found"));
        return order.canBeCancelled();
    }

    // --- MÉTODOS DE ESCRITURA ---

    /* Crear una order sabrosa */
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        // 1. control de idempotencia
        Optional<Order> existingOrder = orderRepository.findByIdempotentKey(request.getIdempotentKey());
        if (existingOrder.isPresent()) {
            return orderDtoMapper.toResponseDTO(existingOrder.get());
        }
        // 2. validar usuario
        userRepository.findById(request.getUserId())
                .orElseThrow(()-> new RuntimeException("User not found: " + request.getIdempotentKey()));
        // 3. mapeo inicial con order-domain temporal con items completos
        Order order = orderDtoMapper.toDomain(request);
        // 4. validar y enriquecer cada item
        List<OrderItem> itemsEnriched = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            // verificar que el item ya no esté enriquecido
            if (item.getProductName() != null) {
                throw new IllegalStateException("Item already enriched");
            }
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(()-> new RuntimeException("Product with id: " + item.getProductId() + " not found"));
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName()
                        + ". Available: " + product.getStock() + ", Requested: " + item.getQuantity());
            }
            product.reduceStock(item.getQuantity());
            productRepository.save(product);
            // creo un nuevo orderItem con datos completes
            OrderItem itemEnriched = item.assignProductDetails(product.getName(), product.getPrice());
            itemsEnriched.add(itemEnriched);
        }
        // Creo una NUEVA orden sabrosa con los items enriquecidos y gorditos
        Order enrichedOrder = new Order(order.getUserId(), itemsEnriched, order.getIdempotentKey());
        Order savedOrder = orderRepository.save(enrichedOrder);
        return orderDtoMapper.toResponseDTO(savedOrder);
    }

    /* Pagar una orden (cambia estado de PENDING a PAID) */
    public OrderResponseDTO payOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Order with id: " + id + " not found!"));
        order.pay();
        Order saved = orderRepository.save(order);
        return orderDtoMapper.toResponseDTO(saved);
    }

    /* Cancelar una orden y devolver el stock a los productos */
    public OrderResponseDTO cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Order with id: " + id + " not found!"));
        order.cancel();
        // devuelvo el stock de cada producto
        for (var item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(()-> new RuntimeException("Product with id: " + item.getProductId() + " not found!"));
            product.restock(item.getQuantity());
            productRepository.save(product);
        }
        // Guardar la orden cancelada
        Order updatedOrder = orderRepository.save(order);
        return orderDtoMapper.toResponseDTO(updatedOrder);
    }

}
