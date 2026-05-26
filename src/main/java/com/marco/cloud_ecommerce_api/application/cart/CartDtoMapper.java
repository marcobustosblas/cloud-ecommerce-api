package com.marco.cloud_ecommerce_api.application.cart;

import com.marco.cloud_ecommerce_api.domain.cart.Cart;
import com.marco.cloud_ecommerce_api.domain.cart.CartItem;

import java.util.List;

public class CartDtoMapper {

    // CartRequestDTO -> Domain
    public Cart toDomain(CartRequestDTO request) {
        if (request == null) return null;
        // Los items se procesan en el Service uno por uno
        // Por ahora, el carrito se crea vacío y se llena con addItem()
        // List<CartItem> items = request.getItems().stream()
        //         .map(item -> new CartItem(item.getProductId(), item.getQuantity()))
        //         .toList();
        return new Cart(request.getUserId());
        // Nota: Los items se agregan después con addItem()
    }

    // Domain Items (CartItem) -> ResponseDTO (CartItemResponseDTO)
    public CartItemResponseDTO toCartItemResponseDTO(CartItem cartItem) {
        if (cartItem == null) return null;
        return new CartItemResponseDTO(
                cartItem.getProductId(),
                cartItem.getProductName(),
                cartItem.getQuantity(),
                cartItem.getUnitPrice(),
                cartItem.getSubtotal()
        );
    }

    // Domain -> CartResponseDTO
    public CartResponseDTO toResponseDTO(Cart cart) {
        if (cart == null) return null;
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(this::toCartItemResponseDTO)
                .toList();
        return new CartResponseDTO(
                cart.getId(), cart.getUserId(), items, cart.getTotal(), cart.isEmpty());
    }

}
