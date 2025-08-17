package com.dauducbach.cart_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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

    @Field(type = FieldType.Text)
    String userId;

    @Field(type = FieldType.Nested)
    List<CartItemIndex> listItems;
}
