package com.lumen.inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import com.lumen.snow.service.SNOWRestClient;
import com.lumen.snow.service.impl.SNOWRestClientImpl;

@Configuration
public class ServiceNowConfig {

    @Value("${snow.api.base.url}")
    private String snowBaseUrl;
    @Value("${snow.api.username}")
    private String snowUsername;
    @Value("${snow.api.password}")
    private String snowPassword;
    @Value("${snow.api.clientId}")
    private String snowClientId;
    @Value("${snow.api.clientSecret}")
    private String snowClientSecret;

    @SuppressWarnings("null")
    @Bean
    public WebClient snowWebClient() {
        return WebClient.builder()
            .baseUrl(snowBaseUrl)
            .defaultHeader("Accept", "application/json")
            .defaultHeader("Content-Type", "application/json")
            .build();
    }

    @Bean
    public SNOWRestClient snowRestClient(WebClient snowWebClient) {
        return new SNOWRestClientImpl(snowWebClient);
    }
}
