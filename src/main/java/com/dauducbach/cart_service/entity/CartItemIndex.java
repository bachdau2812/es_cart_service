package com.dauducbach.cart_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CartItemIndex {
    String itemId;
    String productId;
    String productName;         // variant name
    String productImageUrl;     // avatar
    String productBrand;
    String description;
    BigDecimal price;
    int quantity;
}
