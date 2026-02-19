
package com.lumen.inventory.service.impl;

import com.lumen.inventory.service.internet.InternetInventoryService;
import com.lumen.inventory.service.port.PortInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lumen.inventory.dto.InventoryQueryParams;
import com.lumen.inventory.dto.responses.GetInventoryResponse;
import com.lumen.inventory.service.InventoryQueryService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of InventoryQueryService following SOLID principles and Account Service pattern.
 *
 * <p>This service acts as an entry point for inventory queries. It delegates Internet-specific logic
 * to {@link com.lumen.inventory.service.internet.InternetInventoryService} to follow the Single Responsibility Principle (SRP).
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Validate bearer token and extract customer numbers</li>
 *   <li>Check service type and delegate to the appropriate specialized service (e.g., InternetInventoryService)</li>
 *   <li>Return the response to the client</li>
 * </ul>
 *
 * <p>Key Design Patterns:</p>
 * <ul>
 *   <li>Delegation for SRP</li>
 *   <li>Separation of concerns via client interfaces</li>
 *   <li>Follows the same structure as BillingAccountQueryService</li>
 * </ul>
 *
 * @author API Development Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Service
@Slf4j
public class InventoryQueryServiceImpl implements InventoryQueryService {



    @Autowired
    private InternetInventoryService internetInventoryService;

    @Autowired
    private PortInventoryService portInventoryService;

    /**
     * Retrieves customer inventory based on query parameters.
     * Delegates Internet-specific logic to {@link com.lumen.inventory.service.internet.InternetInventoryService}.
     * Only customerNumbers, serviceType, and serviceId are supported as query parameters.
     *
     * @param queryParams Query parameters (customerNumbers, serviceType, serviceId)
     * @return ResponseEntity containing inventory response
     */
    @Override
    public ResponseEntity<GetInventoryResponse> getCustomerInventory(InventoryQueryParams queryParams) {
        log.info("Processing inventory request with params: {}", queryParams.getQuerySummary());


        // Using constants from InventoryQueryParams
        return queryParams.serviceType()
            .map(type -> {
                switch (type) {
                    case InventoryQueryParams.SERVICE_TYPE_INTERNET:
                        // Internet flow: SNOW, AM, GLM, filtering
                        return getInventoryInternetList(queryParams);
                    case InventoryQueryParams.SERVICE_TYPE_PORT:
                        // Port logic can be added here in the future
                        return getInventoryPortList(queryParams);
                }
                // This should never be reached if validation is correct
                throw new IllegalStateException("Unexpected serviceType: " + type);
            }).get();
    }

    /**
     * Handles Internet service type inventory retrieval.
     * Delegates to {@link com.lumen.inventory.service.internet.InternetInventoryService#getInventoryInternetList(InventoryQueryParams)}.
     *
     * @param queryParams Query parameters
     * @return ResponseEntity with inventory data
     */
    private ResponseEntity<GetInventoryResponse> getInventoryInternetList(InventoryQueryParams queryParams) {
        return internetInventoryService.getInventoryInternetList(queryParams);
    }

  

    /**
     * Handles Port service type inventory retrieval.
     * Delegates to {@link com.lumen.inventory.service.port.PortInventoryService#getInventoryPortList(InventoryQueryParams)}.
     *
     * @param queryParams Query parameters
     * @return ResponseEntity with inventory data (currently NOT_IMPLEMENTED)
     */
    private ResponseEntity<GetInventoryResponse> getInventoryPortList(InventoryQueryParams queryParams) {
        return portInventoryService.getInventoryPortList(queryParams);
    }

}
