package com.dauducbach.cart_service.repository;

import com.dauducbach.cart_service.entity.CartIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartIndexRepository extends ElasticsearchRepository<CartIndex, String> {

}
