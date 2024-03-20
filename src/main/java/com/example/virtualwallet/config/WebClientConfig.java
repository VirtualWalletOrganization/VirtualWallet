package com.example.virtualwallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@PropertySource("classpath:application.properties")
public class WebClientConfig {

    @Bean
    public WebClient dummyAppuWebClient() {
        return WebClient.create();
    }
}