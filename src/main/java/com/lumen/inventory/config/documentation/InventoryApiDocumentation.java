package com.lumen.inventory.config.documentation;

/**
 * Centralized OpenAPI documentation strings for Inventory API.
 * Separates documentation concerns from controller logic.
 */
public final class InventoryApiDocumentation {
    
    private InventoryApiDocumentation() {
        // Utility class
    }
    
    public static final class GetInventory {
        public static final String SUMMARY = "Get customer inventory";
        public static final String DESCRIPTION = """
            Retrieve inventory for multiple customers with advanced filtering and pagination.
            Supports multi-criteria filtering including product code, service type, and status.
            """;
        public static final String OPERATION_ID = "getCustomerInventory";
    }
    
    public static final class Parameters {
        public static final String CUSTOMER_NUMBERS_DESCRIPTION = 
            "List of customer numbers to query inventory for";
        public static final String PAGE_NUMBER_DESCRIPTION = 
            "Page number for pagination (1-based)";
        public static final String PAGE_SIZE_DESCRIPTION = 
            "Number of records per page (max 100)";
        public static final String PRODUCT_CODE_DESCRIPTION = 
            "Product code filter (e.g., AVPN, Internet)";
        public static final String SERVICE_TYPE_DESCRIPTION = 
            "Service type filter";
        public static final String STATUS_DESCRIPTION = 
            "Status filter (e.g., Active, Suspended)";
        public static final String SERVICE_ID_DESCRIPTION = 
            "Service ID for specific service queries";
        public static final String BILLING_ACCOUNT_NUMBER_DESCRIPTION = 
            "Billing account number filter";
        public static final String NAAS_ENABLED_DESCRIPTION = 
            "Filter by NAAS enabled status";
        public static final String INCLUDE_DESCRIPTION = 
            "Additional data to include in response";
        public static final String CORRELATION_ID_DESCRIPTION = 
            "Correlation ID for distributed tracing";
    }
    
    public static final class Responses {
        public static final String SUCCESS_200_DESCRIPTION = 
            "Successfully retrieved inventory";
        public static final String BAD_REQUEST_400_DESCRIPTION = 
            "Invalid request parameters or missing required fields";
        public static final String UNAUTHORIZED_401_DESCRIPTION = 
            "Authentication failure or invalid credentials";
        public static final String INTERNAL_ERROR_500_DESCRIPTION = 
            "Internal server error occurred";
    }
    
    public static final class Examples {
        public static final String SUCCESSFUL_INVENTORY_RESPONSE = 
            "/static/api-docs/responses/successful-inventory-response.json";
        public static final String EMPTY_INVENTORY_RESPONSE = 
            "/static/api-docs/responses/empty-inventory-response.json";
        public static final String INVALID_PAGE_NUMBER = 
            "/static/api-docs/errors/invalid-page-number.json";
        public static final String INVALID_PAGE_SIZE = 
            "/static/api-docs/errors/invalid-page-size.json";
        public static final String AUTHENTICATION_ERROR = 
            "/static/api-docs/errors/authentication-error.json";
        public static final String INTERNAL_SERVER_ERROR = 
            "/static/api-docs/errors/internal-server-error.json";
    }
}
