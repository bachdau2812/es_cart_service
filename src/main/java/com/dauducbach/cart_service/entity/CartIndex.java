package com.dauducbach.cart_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

@Document(indexName = "carts")
public class CartIndex {
    @Id
    String id;
    String userId;

    List<CartItem> listItems;

    LocalDate createAt;
    LocalDate updateAt;
}
