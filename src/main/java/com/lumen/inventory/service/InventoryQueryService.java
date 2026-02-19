package com.lumen.inventory.service;

import org.springframework.http.ResponseEntity;

import com.lumen.inventory.dto.InventoryQueryParams;
import com.lumen.inventory.dto.responses.GetInventoryResponse;

/**
 * Service interface for inventory query operations.
 * Follows single responsibility principle - handles only inventory queries.
 */
public interface InventoryQueryService {
    
    /**
     * Retrieves customer inventory based on query parameters.
     * 
     * @param queryParams Query parameters including filters and pagination
     * @return ResponseEntity containing inventory response
     */
    ResponseEntity<GetInventoryResponse> getCustomerInventory(InventoryQueryParams queryParams);
}
