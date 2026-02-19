package com.lumen.inventory.config.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.lumen.snow.service.SNOWRestClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * ServiceNow client for inventory operations.
 */
@Component
public class SnowClient {

    @Autowired
    private SNOWRestClient snowRestClient;

    public ResponseEntity<String> getInventoryInternetInformationBasedOnMultipleCustomers(
            String customerNumbers,
            Optional<Integer> pageNumber,
            Optional<Integer> pageSize,
            int maxPageSize,
            Optional<String> status) {
        Mono<ResponseEntity<String>> responseMono = snowRestClient
            .getInventoryInternetInformationBasedOnMultipleCustomers(
                customerNumbers, pageNumber, pageSize, String.valueOf(maxPageSize), status);
        return responseMono.block();
    }
}
