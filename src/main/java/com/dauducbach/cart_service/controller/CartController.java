package com.dauducbach.cart_service.controller;

import com.dauducbach.cart_service.dto.request.GetCartCheckout;
import com.dauducbach.cart_service.entity.Cart;
import com.dauducbach.cart_service.entity.CartCheckout;
import com.dauducbach.cart_service.service.CartOperator;
import com.dauducbach.cart_service.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class CartController {
    CartService cartService;
    CartOperator cartOperator;

    @GetMapping("/get")
    public Cart getCart() {
        return cartService.getCart();
    }

    @GetMapping("/add")
    public String addItem(@RequestParam String productId, @RequestParam int quantity) {
        return cartService.addItems(productId, quantity);
    }

    @DeleteMapping("/delete")
    public String deleteItem(@RequestParam String itemId) {
        return cartService.deleteItem(itemId);
    }

    @GetMapping("/cart_checkout")
    public CartCheckout getCartCheckout(GetCartCheckout getCartCheckout) {
        return cartOperator.getCheckout(getCartCheckout);
    }

}
