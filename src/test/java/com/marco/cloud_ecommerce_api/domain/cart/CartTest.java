package com.marco.cloud_ecommerce_api.domain.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    private Cart cart;
    private UUID userId;
    private UUID productId;
    private final BigDecimal defaultPrice = new BigDecimal("50.00");

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        cart = new Cart(userId);
    }

    @Test
    void shouldCreateEmptyCart() {
        assertNotNull(cart.getId());
        assertEquals(userId, cart.getUserId());
        assertTrue(cart.isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotal());
    }

    @Test
    void shouldAddItemToCart() {
        cart.addItem(productId, "Zapatillas", 3, defaultPrice);

        assertEquals(1, cart.getItems().size());
        assertEquals(new BigDecimal("150.00"), cart.getTotal());
    }

    @Test
    void shouldSumQuantitiesWhenAddingSameProduct() {
        cart.addItem(productId, "Zapatillas", 3, defaultPrice);
        cart.addItem(productId, "Zapatillas", 5, defaultPrice);

        CartItem item = cart.getItems().getFirst();
        assertEquals(8, item.getQuantity());
        assertEquals(new BigDecimal("400.00"), item.getSubtotal());
        assertEquals(new BigDecimal("400.00"), cart.getTotal());
    }

    @Test
    void shouldRemoveItemFromCart() {
        cart.addItem(productId, "Zapatillas", 3, defaultPrice);
        cart.removeItem(productId);
        assertTrue(cart.isEmpty());
    }

}
