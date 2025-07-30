package com.dauducbach.cart_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductImageResponse {
    String id;
    String imageUrl;
    int isThumbnail;
    String altText;         // Anh mo ta khi loi
    LocalDate createdAt;
}
