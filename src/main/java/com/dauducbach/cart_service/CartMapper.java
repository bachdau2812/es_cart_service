package com.dauducbach.cart_service;

import com.dauducbach.cart_service.entity.Cart;
import com.dauducbach.cart_service.entity.CartIndex;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartIndex toCartIndex(Cart cart);
}
