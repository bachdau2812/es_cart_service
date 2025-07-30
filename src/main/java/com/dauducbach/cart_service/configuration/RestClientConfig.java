package com.dauducbach.cart_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestClientConfig {
    @Bean
    RestClient restClient() {
        return RestClient.builder().build();
    }
}

