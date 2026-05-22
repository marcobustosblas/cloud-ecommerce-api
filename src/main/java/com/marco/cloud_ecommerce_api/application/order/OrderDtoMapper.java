package com.marco.cloud_ecommerce_api.application.order;

import com.marco.cloud_ecommerce_api.domain.order.Order;
import com.marco.cloud_ecommerce_api.domain.order.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderDtoMapper {

    // RequestDTO → Domain (para crear una orden)
    public Order toDomain(OrderRequestDTO request) {
        if (request == null) return null;
        // Convertir cada OrderItemRequestDTO a OrderItem (dominio)
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        "", // productName se asignará en el servicio (con consulta a BD)
                        item.getQuantity(),
                        BigDecimal.ZERO // unitPrice se asignará en el servicio
                )).toList();
        return new Order(request.getUserId(), orderItems, request.getIdempotentKey());
    }

    // Domain → ResponseDTO
    public OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) return null;
        // Convertir cada OrderItem (dominio) en OrderItemResponseDTO
        List<OrderItemResponseDTO> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProductId(),
                        item.getProductName(), // ← ahora sí tiene nombre
                        item.getQuantity(),
                        item.getUnitPrice(), // ← ahora sí tiene precio
                        item.getSubtotal()
                )).toList();
        return new OrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                itemResponses,
                order.getTotal(),
                order.getCreatedAt(),
                order.getIdempotentKey()
        );
    }

}
