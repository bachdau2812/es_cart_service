package com.dauducbach.cart_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CartItem {
    String id;
    String productId;
    String productName;         // variant name
    String productImageUrl;     // avatar
    String productBrand;
    String description;
    BigDecimal price;
    int quantity;
    LocalDate addAt;
}
