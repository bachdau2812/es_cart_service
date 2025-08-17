package com.dauducbach.cart_service.mapper;

import com.dauducbach.cart_service.entity.CartItem;
import com.dauducbach.cart_service.entity.CartItemIndex;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    CartItemIndex toCartItemIndex(CartItem cartItem);
}
