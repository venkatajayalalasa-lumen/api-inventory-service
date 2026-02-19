package com.lumen.inventory.dto;

import java.util.List;
import java.util.Optional;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) representing query parameters for inventory operations.
 */
@Schema(
    description = "Query parameters for inventory retrieval operations.",
    example = "{\n  \"customerNumbers\": [\"15182\", \"6887\", \"2-LK2D1Y\"],\n  \"serviceType\": \"Internet\",\n  \"serviceId\": \"SVC123456\"\n}"
)
public record InventoryQueryParams(
    @Schema(description = "List of customer numbers to query inventory for")
    List<String> customerNumbers,

    @Schema(description = "Service type filter", example = "Internet")
    Optional<String> serviceType,

    @Schema(description = "Service ID filter", example = "SVC123456")
    Optional<String> serviceId
) {
    public static final String SERVICE_TYPE_INTERNET = "Internet";
    public static final String SERVICE_TYPE_PORT = "Port";

    public InventoryQueryParams {
        if (customerNumbers == null || customerNumbers.isEmpty()) {
            throw new IllegalArgumentException("At least one customer number is required");
        }
        // Validate serviceType if present
        if (serviceType != null && serviceType.isPresent()) {
            String type = serviceType.get();
            if (!SERVICE_TYPE_INTERNET.equalsIgnoreCase(type) && !SERVICE_TYPE_PORT.equalsIgnoreCase(type)) {
                throw new IllegalArgumentException("Invalid serviceType: " + type + ". Allowed values: Internet, Port");
            }
        }
    }

    /**
     * Returns a summary string of the query parameters for logging/debugging.
     */
    public String getQuerySummary() {
        var summary = new StringBuilder("InventoryQuery: ");
        summary.append("customerNumbers=").append(customerNumbers);
        summary.append(", serviceType=").append(serviceType.orElse("N/A"));
        summary.append(", serviceId=").append(serviceId.orElse("N/A"));
        return summary.toString();
    }
}
