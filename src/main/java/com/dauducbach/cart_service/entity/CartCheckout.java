package com.dauducbach.cart_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

public class CartCheckout {
    int quantity;
    BigDecimal totalPrice;
}
