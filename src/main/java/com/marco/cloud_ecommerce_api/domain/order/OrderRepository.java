package com.marco.cloud_ecommerce_api.domain.order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    Optional<Order> findByIdempotentKey(String idempotentKey);
}
