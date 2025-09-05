package com.dauducbach.cart_service;

import com.dauducbach.cart_service.entity.Cart;
import com.dauducbach.cart_service.repository.CartIndexRepository;
import com.dauducbach.cart_service.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class CartServiceApplicationTests {

	@MockitoBean
	public RedisConnectionFactory connectionFactory;

	@MockitoBean
	public RedisTemplate<String, String> redisTemplate;

	@MockitoBean
	public RedisTemplate<String, Cart> redisTemplateCart;

	@MockitoBean
	public CartIndexRepository cartIndexRepository;

	@MockitoBean
	public CartRepository cartRepository;

	@Test
	void contextLoads() {
	}

}
