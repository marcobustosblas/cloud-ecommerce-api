package com.marco.cloud_ecommerce_api.infrastructure.api.controller;

import com.marco.cloud_ecommerce_api.application.order.OrderRequestDTO;
import com.marco.cloud_ecommerce_api.application.order.OrderResponseDTO;
import com.marco.cloud_ecommerce_api.application.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // -- commands (Escritura)

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDTO> create(
            @Valid @RequestBody OrderRequestDTO request,
            @RequestHeader(value = "Idempotent-Key", required = true) String idempotentKey
            ) {
        // Atrapo la clave del Header de forma segura y se la inyecto al DTO
        request.setIdempotentKey(idempotentKey);

        OrderResponseDTO created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<OrderResponseDTO> pay(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.payOrder(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    // -- queries (Lectura)

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/idempotent/{key}")
    public ResponseEntity<OrderResponseDTO> findByIdempotentKey(@PathVariable String key) {
        return ResponseEntity.ok(orderService.findByIdempotentKey(key));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> findByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.findByUserId(userId));
        // aquí me confundí, es: 1 usuario puede tener muchas órdenes
    }

    @GetMapping("/{id}/can-be-paid")
    public ResponseEntity<Boolean> canBePaid(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.canBePaid(id));
    }

    @GetMapping("/{id}/can-be-cancelled")
    public ResponseEntity<Boolean> canBeCancelled(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.canBeCancelled(id));
    }

}
