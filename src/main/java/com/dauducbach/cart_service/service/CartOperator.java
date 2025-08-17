package com.dauducbach.cart_service.service;

import com.dauducbach.cart_service.dto.request.GetCartCheckout;
import com.dauducbach.cart_service.entity.Cart;
import com.dauducbach.cart_service.entity.CartCheckout;
import com.dauducbach.cart_service.entity.CartItem;
import com.dauducbach.cart_service.repository.CartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartOperator {
    CartRepository cartRepository;
    RedisTemplate<String, Cart> redisTemplate;

    public List<CartItem> getByProductBrand(String productBrand) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();

        return cartRepository.findByUserId(userId)
                .getListItems()
                .stream()
                .filter(cartItem -> cartItem.getProductBrand().equals(productBrand))
                .toList();
    }

    public CartCheckout getCheckout(GetCartCheckout getCartCheckout) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();

        var cart = redisTemplate.opsForValue().get("cart:" + userId);

        assert cart != null;

        BigDecimal totalPrice = new BigDecimal(0);
        int quantity = 0;

        Map<String, CartItem> cartItemMap = new HashMap<>();
        cart.getListItems()
                .forEach(cartItem -> {
                    cartItemMap.putIfAbsent(cartItem.getItemId(), cartItem);
                });

        for (String itemId : getCartCheckout.getItemId()) {
            CartItem cartItem = cartItemMap.get(itemId);
            totalPrice = totalPrice.add(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            quantity += cartItem.getQuantity();;
        }

        return CartCheckout.builder()
                .quantity(quantity)
                .totalPrice(totalPrice)
                .build();
    }
}
