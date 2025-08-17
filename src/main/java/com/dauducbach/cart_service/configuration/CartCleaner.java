package com.dauducbach.cart_service.configuration;

import com.dauducbach.cart_service.entity.Cart;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class CartCleaner {
    private final RedisTemplate<String, Cart> redisTemplate;

    @PreDestroy
    public void onDestroy() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();

        String key = "cart:" + userId;
        redisTemplate.delete(key);
        System.out.println("🧹 Đã xóa key Redis trước khi ứng dụng dừng: " + key);
    }
}
