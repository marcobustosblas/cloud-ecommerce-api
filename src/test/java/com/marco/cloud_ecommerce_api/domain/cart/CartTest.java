package com.marco.cloud_ecommerce_api.domain.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    void shouldUpdateQuantity() {
        cart.addItem(productId, "Zapatos", 2, defaultPrice);
        cart.updateQuantity(productId, 5);

        CartItem item = cart.getItems().getFirst();
        assertEquals(5, item.getQuantity());
        assertEquals(new BigDecimal("250.00"), cart.getTotal());
        assertEquals(new BigDecimal("250.00"), item.getSubtotal());
    }

    @Test
    void shouldRemoveItemWhenUpdateQuantityToZero() {
        cart.addItem(productId, "Zapatos", 3, defaultPrice);
        cart.updateQuantity(productId, 0);

        assertTrue(cart.isEmpty());
    }

    @Test
    void shouldClearCart() {
        cart.addItem(productId, "Zapatillas", 2, defaultPrice);
        UUID secondProduct = UUID.randomUUID();
        cart.addItem(secondProduct, "Mochila", 3, new BigDecimal("30.00"));
        cart.clear();

        assertTrue(cart.isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotal());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void shouldReturnInmutableList() {
        cart.addItem(productId, "Zapatillas", 3, defaultPrice);

        List<CartItem> items = cart.getItems();

        assertThrows(UnsupportedOperationException.class, ()->{
            items.add(new CartItem(UUID.randomUUID(), "Colado", 3, BigDecimal.ONE));
        });
    }

    @Test
    void shouldBeEqualsById() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Cart cart1 = new Cart(cartId, userId, new ArrayList<>());
        Cart cart2 = new Cart(cartId, userId, new ArrayList<>());

        assertEquals(cart1, cart2);
    }

}
