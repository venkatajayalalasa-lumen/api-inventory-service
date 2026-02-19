# API Inventory Service

## Overview

The API Inventory Service provides comprehensive inventory management capabilities following the **Account Service pattern** and incorporating logic from **ServiceManagementV4.java** for Internet service type inventory queries.

## Architecture

### Design Pattern

This service follows **SOLID principles** and uses the same architectural pattern as the Account Service:

- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Interfaces can be substituted with implementations
- **Interface Segregation**: Client-specific interfaces
- **Dependency Inversion**: Depends on abstractions, not concretions

### Key Components

```
api-inventory-service/
├── controller/
│   └── InventoryController.java              # HTTP endpoint handler
├── service/
│   ├── InventoryQueryService.java (interface) # Service contract
│   └── impl/
│       └── InventoryQueryServiceImpl.java     # Service implementation
├── service/client/                            # External service clients
│   ├── ValidationClient.java
│   ├── SNOWClient.java
│   ├── AzureClient.java
│   ├── AccountManagementClient.java
│   ├── GLMClient.java
│   ├── impl/                                  # Client implementations
│   └── model/                                 # Client models
├── dto/
│   ├── InventoryQueryParams.java              # Request parameters
│   └── responses/
│       ├── InventoryResponse.java             # Response wrapper
│       └── ProductInventory.java              # Inventory item
└── config/
    └── documentation/
        └── InventoryApiDocumentation.java     # OpenAPI documentation
```

## Internet Service Type Flow

### Requirement

**For `serviceType = Internet`**:

1. **Single ServiceNow Call**: Make one SNOW call with all customer numbers (comma-separated list)
2. **Enrich with GLM**: Call GLM to enrich location/site data
3. **Enrich with AM**: Call Account Management to map billing account numbers

### Implementation Flow

```
┌─────────────────────────────────────────────────────────────┐
│  GET /Naas/v1/ProductInventory/inventory?serviceType=Internet
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
          ┌───────────────────────┐
          │ InventoryController   │
          └───────────┬───────────┘
                      │
                      ▼
          ┌───────────────────────────────┐
          │ InventoryQueryServiceImpl     │
          │ getCustomerInventory()        │
          └───────────┬───────────────────┘
                      │
                      ▼
          ┌───────────────────────────────┐
          │ If serviceType == "Internet"  │
          └───────────┬───────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│              getInventoryInternetList()                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Step 1: Validate Bearer Token                             │
│          ┌──────────────────────┐                          │
│          │  ValidationClient    │                          │
│          └──────────────────────┘                          │
│                                                             │
│  Step 2: Get Customer Numbers (from query params)          │
│                                                             │
│  Step 3: Call ServiceNow (SINGLE CALL)                     │
│          ┌──────────────────────┐                          │
│          │    SNOWClient        │                          │
│          │  - customerNumbers   │                          │
│          │    (comma-separated) │                          │
│          │  - azureToken        │                          │
│          │  - pageNumber        │                          │
│          │  - pageSize          │                          │
│          │  - status            │                          │
│          └──────────────────────┘                          │
│                    │                                        │
│                    ▼                                        │
│          Returns List<Product>                             │
│                                                             │
│  Step 4: Enrich with Account Management (PARALLEL)         │
│          ┌──────────────────────┐                          │
│          │ AccountMgmtClient    │                          │
│          │  - Extract BANs      │                          │
│          │  - Map to Invoice#   │                          │
│          │  (Multithreaded)     │                          │
│          └──────────────────────┘                          │
│                                                             │
│  Step 5: Build Service Inventory List (PARALLEL)           │
│          ┌──────────────────────┐                          │
│          │  Process Products    │                          │
│          │  (Multithreaded)     │                          │
│          └──────────────────────┘                          │
│                                                             │
│  Step 6: Enrich with GLM (PARALLEL)                        │
│          ┌──────────────────────┐                          │
│          │     GLMClient        │                          │
│          │  - Get Site Locations│                          │
│          │  (Batched calls)     │                          │
│          └──────────────────────┘                          │
│                                                             │
│  Step 7: Return InventoryResponse                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Key Features

### 1. **Single ServiceNow Call**

Unlike traditional approaches that make multiple calls per customer, this implementation:

```java
// Single call with all customers
ResponseEntity<String> response = snowClient
    .getInventoryInternetInformationBasedOnMultipleCustomers(
        "12345,67890,11223",  // Comma-separated customer numbers
        azureToken,
        pageNumber,
        pageSize,
        maxPageSize,
        status
    );
```

### 2. **Multithreaded Enrichment**

Uses **Java 21 Virtual Threads** for parallel processing:

- **BAN to Invoice Display Mapping**: ForkJoinPool with configurable threads
- **Service Inventory Building**: ExecutorService with thread pooling
- **GLM Location Enrichment**: Batch processing with concurrent calls

### 3. **SOLID Architecture**

- **Interfaces**: All external dependencies are abstracted (SNOWClient, GLMClient, etc.)
- **Separation of Concerns**: Controller → Service → Clients (clear boundaries)
- **Testability**: Easy to mock clients for unit testing

## Configuration

### Required Properties

```properties
# ServiceNow Configuration
snow.api.base.url=https://snow-api.example.com
snow.api.inventory.endpoint=/api/lumen/inventory/internet
snow.naas.detail.inventory.max.pagesize=100

# Account Management Configuration
am.api.base.url=https://am-api.example.com
am.api.invoice.endpoint=/api/account/invoice

# GLM Configuration
glm.api.base.url=https://glm-api.example.com
glm.api.location.endpoint=/api/location

# Azure AD Configuration
azure.client.id=${AZURE_CLIENT_ID}
azure.client.secret=${AZURE_CLIENT_SECRET}
azure.tenant.id=${AZURE_TENANT_ID}

# Service Configuration
naas.inventory.thread.allocation.count=10
naas.inventoryapi.valid.attribute.list=UNI Service ID,Router Type,Product Code
naas.bearer.token.validation.header=Authorization
naas.product.specification.name.for.SNow=Internet
```

## API Endpoints

### Get Product Inventory

**Endpoint**: `GET /Naas/v1/ProductInventory/inventory`

**Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| customerNumbers | List<String> | Yes | List of customer numbers |
| serviceType | String | No | Service type filter (e.g., "Internet") |
| pageNumber | Integer | No | Page number (default: 1) |
| pageSize | Integer | No | Page size (default: 20, max: 100) |
| productCode | String | No | Product code filter |
| naasEnabled | Boolean | No | NAAS enabled filter |
| billingAccountNumber | String | No | Billing account filter |
| serviceId | String | No | Service ID filter |
| status | String | No | Status filter (Active, Suspended, All) |

**Headers**:
- `Authorization`: Bearer token (required)
- `x-correlation-id`: Request tracking ID (optional)

**Example Request**:

```bash
curl -X GET "http://localhost:8080/Naas/v1/ProductInventory/inventory?customerNumbers=15182,6887&serviceType=Internet&pageNumber=1&pageSize=20" \
  -H "Authorization: Bearer <token>" \
  -H "x-correlation-id: req-12345"
```

**Example Response**:

```json
{
  "inventory": [
    {
      "serviceId": "SVC123456",
      "customerNumber": "15182",
      "productCode": "INTERNET",
      "serviceType": "Internet",
      "status": "Active",
      "billingAccountNumber": "BAN123456",
      "naasEnabled": true
    }
  ],
  "pageNumber": 1,
  "pageSize": 20,
  "resultCount": 1
}
```

## Building and Running

### Prerequisites

- Java 21 or higher
- Maven 3.8+

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

### Swagger UI

Access API documentation at: `http://localhost:8080/swagger-ui.html`

## Implementation Notes

### From ServiceManagementV4.java

The following logic is extracted from `ServiceManagementV4.java`:

1. ✅ **Single SNOW call** with multiple customer numbers
2. ✅ **BAN enrichment** via Account Management (parallel processing)
3. ✅ **GLM enrichment** for location details (batch processing)
4. ✅ **Multithreaded processing** using ForkJoinPool and ExecutorService
5. ✅ **Pagination support** from SNOW response headers
6. ✅ **Status filtering** at SNOW level

### Account Service Pattern

The following patterns are adopted from Account Service:

1. ✅ **Service interface** with implementation
2. ✅ **Client abstractions** for external dependencies
3. ✅ **DTO-based** request/response handling
4. ✅ **Comprehensive validation** in query params
5. ✅ **OpenAPI documentation** in separate class
6. ✅ **Proper error handling** with status codes

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify
```

## Migration Guide

To migrate existing code:

1. **Update ServiceNow calls**: Use `SNOWClient.getInventoryInternetInformationBasedOnMultipleCustomers()`
2. **Remove multiple SNOW calls**: Consolidate into single call with comma-separated customer numbers
3. **Update enrichment logic**: Use parallel processing with `AccountManagementClient` and `GLMClient`
4. **Update response mapping**: Map to new `ProductInventory` DTO

## Troubleshooting

### Common Issues

**Issue**: `Token missing in header`
- **Solution**: Ensure `Authorization` header is present with Bearer token

**Issue**: `No customer numbers found`
- **Solution**: Verify customer numbers are passed in query parameters

**Issue**: `SNOW API timeout`
- **Solution**: Increase timeout or reduce page size

## License

Copyright © 2026 Lumen Technologies

## Contact

API Development Team - api-dev@lumen.com
