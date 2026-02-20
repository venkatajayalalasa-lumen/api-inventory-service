package com.lumen.inventory.config;

import com.lumen.resourceapi.service.InventoryPortService;
import com.lumen.resourceapi.service.impl.InventoryPortServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import io.micrometer.observation.ObservationRegistry;


@Configuration
public class ResourceApiConfig {
    
    /**
     * Base URL for the Resource API service.
     */
    @Value("${resource.api.entitlement.base-url}")
    private String resourceApiBaseUrl;
    
    /**
     * Authentication token for Resource API service.
     */
    @Value("${resource.api.auth.token}")
    private String authToken;
    
    /**
     * Authentication token header name.
     */
    @Value("${resource.api.auth.header:Authorization}")
    private String authHeaderName;
    
    /**
     * Authentication token prefix (e.g., Bearer, Token).
     */
    @Value("${resource.api.auth.prefix:Bearer}")
    private String authTokenPrefix;

    /**
     * Creates a WebClient specifically for Resource API with authentication headers.
     * This WebClient is INDEPENDENT from accountManagementWebClient.
     * Uses a FRESH builder to avoid inheriting OAuth filters.
     * 
     * @param customExchangeStrategies the exchange strategies for buffer limits
     * @return configured WebClient instance for Resource API
     */
    @SuppressWarnings("null")
    @Bean(name = "resourceApiWebClient", autowireCandidate = false)
    public WebClient resourceApiWebClient(ExchangeStrategies customExchangeStrategies,
                                            ExchangeFilterFunction logRequest,
                                            ObservationRegistry observationRegistry) {
        WebClient.Builder builder = WebClient.builder()
            .baseUrl(resourceApiBaseUrl)
            .exchangeStrategies(customExchangeStrategies)
            .observationRegistry(observationRegistry)
            .filter(logRequest);

        if (authToken != null && !authToken.isEmpty()) {
            String tokenValue = authTokenPrefix.isEmpty() 
                ? authToken : authTokenPrefix + " " + authToken;
            builder.defaultHeader(authHeaderName, tokenValue);
        }
        return builder.build();
    }
    

    @Bean
    @Primary
    public InventoryPortService inventoryPortClient(ExchangeStrategies customExchangeStrategies,
                                            ExchangeFilterFunction logRequest,
                                            ObservationRegistry observationRegistry) {
        return new InventoryPortServiceImpl(resourceApiWebClient(customExchangeStrategies, logRequest, observationRegistry));
    }
}