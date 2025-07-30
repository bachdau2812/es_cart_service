package com.dauducbach.cart_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

public class ProductResponse {

    String id;
    String productLine;
    String productName;
    String productCategoryId;
    String productBrandId;
    String descriptions;
    String shortDescriptions;
    BigDecimal price;
    int purchased_count;

    List<ProductImageResponse> productImages;

    List<AttributeValueResponse> attributes;
}
