package com.dauducbach.cart_service.service;

import com.dauducbach.cart_service.CartMapper;
import com.dauducbach.cart_service.dto.response.ProductResponse;
import com.dauducbach.cart_service.entity.Cart;
import com.dauducbach.cart_service.entity.CartItem;
import com.dauducbach.cart_service.repository.CartIndexRepository;
import com.dauducbach.cart_service.repository.CartRepository;
import com.dauducbach.event.CartEvent;
import com.dauducbach.event.InventoryEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class CartService {
    RestClient restClient;
    CartRepository cartRepository;
    RedisTemplate<String, Cart> redisTemplateCart;
    KafkaTemplate<String, Object> kafkaTemplate;
    CartIndexRepository cartIndexRepository;
    CartMapper cartMapper;

    public Cart createCart() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = "Bearer " + jwt.getTokenValue();
        String userId = jwt.getSubject();

        Cart cart = Cart.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .listItems(new ArrayList<>())
                .createAt(LocalDate.now())
                .updateAt(LocalDate.now())
                .build();

        // Save Cart index to Elasticsearch
        var cartIndex = cartMapper.toCartIndex(cart);
        cartIndexRepository.save(cartIndex);

        return cartRepository.save(cart);
    }

    public void checkCart() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();
        Cart cart = redisTemplateCart.opsForValue().get("cart:" + userId);

        if (cart == null) {
            cart = cartRepository.findByUserId(userId);

            if (cart == null) {
                cart = createCart();
            }

            redisTemplateCart.opsForValue().set("cart:" + userId, cart);
        }
    }

    public String addItems(String productId, int quantity) {
        checkCart();
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = "Bearer " + jwt.getTokenValue();
        String userId = jwt.getSubject();

        String cartKey = "cart:" + userId;
        Cart cart = redisTemplateCart.opsForValue().get(cartKey);

        assert cart != null;
        if (!cart.getListItems().isEmpty()) {
            boolean isExisted = cart.getListItems()
                    .stream()
                    .filter(cartItem -> cartItem.getProductId().equals(productId))
                    .toList()
                    .isEmpty();

            if (!isExisted) {
                List<CartItem> itemEvent = new ArrayList<>();
                List<CartItem> cartItems = cart.getListItems()
                        .stream()
                        .map(cartItem -> {
                            if (cartItem.getProductId().equals(productId)) {
                                itemEvent.add(cartItem);
                                cartItem.setQuantity(cartItem.getQuantity() + 1);
                            }
                            return cartItem;
                        })
                        .toList();
                cart.setListItems(cartItems);
                redisTemplateCart.opsForValue().set(cartKey, cart);

                var cartEvent = CartEvent.builder()
                        .action("ADD")
                        .cartId(cart.getId())
                        .cartItem(itemEvent.getFirst())
                        .build();

                // send event save to database
                sendEvent("update_cart_event", cartEvent);

                // send event to inventory service
                var inventoryEvent = InventoryEvent.builder()
                        .action("ADD")
                        .productId(List.of(itemEvent.getFirst().getProductId()))
                        .build();
                kafkaTemplate.send("cart_update_inventory_event", inventoryEvent);

                return "Added product to cart";
            }
        }

        ProductResponse productResponse  = restClient.get()
                .uri("http://localhost:8082/product/get_by_id/{productId}", productId)
                .header("Authorization", token)
                .retrieve()
                .body(ProductResponse.class);

        assert productResponse != null;
        CartItem cartItem = CartItem.builder()
                .productId(productResponse.getId())
                .productBrand(productResponse.getProductBrandId())
                .productName(productResponse.getProductName())
                .addAt(LocalDate.now())
                .price(productResponse.getPrice())
                .quantity(quantity)
                .description(productResponse.getDescriptions())
                .productImageUrl(productResponse.getProductImages()
                        .stream()
                        .filter(productImageResponse -> productImageResponse.getIsThumbnail() == 1)
                        .toList()
                        .getFirst()
                        .getImageUrl()
                )
                .build();

        cart.getListItems().add(cartItem);

        // send event to database
        var cartEvent = CartEvent.builder()
                .action("ADD")
                .cartId(cart.getId())
                .cartItem(cartItem)
                .build();
        sendEvent("update_cart_event", cartEvent);

        // send event to inventory service
        var inventoryEvent = InventoryEvent.builder()
                .action("ADD")
                .productId(List.of(cartItem.getProductId()))
                .build();
        kafkaTemplate.send("cart_update_inventory_event", inventoryEvent);

        return "Added product to cart";
    }

    public String deleteItem(String itemId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();

        var cart = redisTemplateCart.opsForValue().get("cart:" + userId);
        CartItem[] deletedItem = new CartItem[1];

        assert cart != null;
        cart.getListItems().removeIf(cartItem -> {
            if (cartItem.getId().equals(itemId)) {
                deletedItem[0] = cartItem;
                return true;
            }
            return false;
        });

        if (deletedItem[0] != null) {
            var cartEvent = CartEvent.builder()
                    .action("DELETE")
                    .cartId(cart.getId())
                    .cartItem(deletedItem[0])
                    .build();
            sendEvent("update_cart_event", cartEvent);
        }

        // send event to inventory service
        assert deletedItem[0] != null;
        var inventoryEvent = InventoryEvent.builder()
                .action("ADD")
                .productId(List.of(deletedItem[0].getProductId()))
                .build();
        kafkaTemplate.send("cart_update_inventory_event", inventoryEvent);

        return "Deleted product from cart";
    }

    public Cart getCart() {
        checkCart();

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();

        return redisTemplateCart.opsForValue().get("cart:" + userId);
    }

    public void sendEvent(String topics, CartEvent cartEvent) {
        kafkaTemplate.send(topics, cartEvent);
    }

    @KafkaListener(topics = "update_cart_event")
    public void updateCartDB(@Payload CartEvent cartEvent) {
        var cart = cartRepository.findById(cartEvent.getCartId()).orElseThrow(() -> new RuntimeException("cart not exists"));
        if (cartEvent.getAction().equals("ADD")) {
            cart.getListItems().add(cartEvent.getCartItem());
        }

        if (cartEvent.getAction().equals("DELETE")) {
            cart.getListItems().remove(cartEvent.getCartItem());
        }

        cartRepository.save(cart);
        updateCartIndexRepository(cartEvent);
    }

    public void updateCartIndexRepository(CartEvent cartEvent) {
        var cart = cartIndexRepository.findById(cartEvent.getCartId()).orElseThrow(() -> new RuntimeException("cart not exists"));
        if (cartEvent.getAction().equals("ADD")) {
            cart.getListItems().add(cartEvent.getCartItem());
        }

        if (cartEvent.getAction().equals("DELETE")) {
            cart.getListItems().remove(cartEvent.getCartItem());
        }

        cartIndexRepository.save(cart);
    }
}
