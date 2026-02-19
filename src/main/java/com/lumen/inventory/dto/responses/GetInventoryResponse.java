package com.lumen.inventory.dto.responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetInventoryResponse {
    private List<ServiceInventory> inventoryList;
        private int pageNumber;
        private int pageSize;
        private int resultCount;
    private List<PaginationResponse> pagination;

    public void setPagination(List<PaginationResponse> pagination) {
        this.pagination = pagination;
    }

    public List<PaginationResponse> getPagination() {
        return this.pagination;
    }
/**
 * Data Transfer Object for Inventory Response.
 * <p>Represents the response structure for inventory queries, including pagination metadata and inventory list.</p>
 */
}
