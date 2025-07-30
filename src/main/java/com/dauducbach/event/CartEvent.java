package com.dauducbach.event;

import com.dauducbach.cart_service.entity.CartItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

public class CartEvent {
    String action;
    String cartId;
    CartItem cartItem;
}
