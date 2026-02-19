package com.lumen.inventory.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumen.inventory.config.documentation.InventoryApiDocumentation;
import com.lumen.inventory.dto.InventoryQueryParams;
import com.lumen.inventory.dto.responses.GetInventoryResponse;
import com.lumen.inventory.service.InventoryQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * HTTP endpoint handler for inventory operations (Refactored for SOLID principles).
 * 
 * <p>This controller provides comprehensive HTTP endpoints for inventory management.
 * Following SOLID principles, it delegates all business logic to specialized service classes:</p>
 * <ul>
 *   <li>{@link InventoryQueryService} - Inventory queries and filtering</li>
 * </ul>
 * 
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Paginated inventory retrieval with customizable page sizes</li>
 *   <li>Multi-criteria filtering (product code, service type, status)</li>
 *   <li>Support for multiple customer numbers in a single request</li>
 *   <li>Header-based authentication and request tracking</li>
 *   <li>Comprehensive error handling with meaningful responses</li>
 * </ul>
 * 
 * <p><strong>Design Improvements (v2.0):</strong></p>
 * <ul>
 *   <li><strong>Single Responsibility:</strong> Each service handles one type of operation</li>
 *   <li><strong>Separation of Concerns:</strong> OpenAPI documentation extracted to separate class</li>
 *   <li><strong>Dependency Inversion:</strong> Depends on service abstractions</li>
 *   <li><strong>Open/Closed:</strong> New functionality added via new services without modifying existing code</li>
 * </ul>
 * 
 * @author API Development Team
 * @version 2.0.0
 * @since 1.0.0
 */
@Tag(
    name = "Inventory Management",
    description = """
        Inventory retrieval operations with filtering and pagination.
        Supports authentication via headers and optional request tracking.
        """
)
@RestController
@RequestMapping("/Naas/v1/ProductInventory")
@RequiredArgsConstructor
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryQueryService inventoryQueryService;

    /**
     * Retrieves inventory for multiple customers with advanced filtering and pagination.
     * 
     * <p>This endpoint provides comprehensive inventory retrieval with support for
     * multiple filtering criteria and pagination. It validates request headers for proper
     * authentication and tracking, then processes the request through the service layer.</p>
     * 
     * <p><strong>Pagination:</strong></p>
     * <p>Supports pagination with configurable page sizes. Default values are
     * pageNumber=1 and pageSize=20. Maximum page size is typically limited to prevent
     * performance issues.</p>
     * 
     * <p><strong>Filtering:</strong></p>
     * <p>Multiple filter criteria can be combined for precise inventory selection:
     * <ul>
     *   <li>Customer numbers for multi-customer queries</li>
     *   <li>Product code filtering</li>
     *   <li>Service type filtering</li>
     *   <li>Status-based filtering (active, suspended, etc.)</li>
     *   <li>Service ID for specific service queries</li>
     *   <li>Billing account number filtering</li>
     * </ul></p>
     * 
     * <p><strong>Headers:</strong></p>
     * <p>Optional headers include correlation ID for tracking purposes across
     * distributed systems.</p>
     * 
     * @param customerNumbers List of customer numbers to query inventory for
     * @param pageNumber Optional page number for pagination (default: 1)
     * @param pageSize Optional page size for pagination (default: 20)
     * @param productCode Optional product code filter
     * @param serviceType Optional service type filter
     * @param status Optional status filter
     * @param serviceId Optional service ID filter
     * @param billingAccountNumber Optional billing account number filter
     * @param naasEnabled Optional NAAS enabled filter
     * @param include Optional include parameter for additional data
     * @param correlationId Optional correlation ID for request tracking
     * @param headers All request headers for authentication/tracking
     * 
     * @return {@link ResponseEntity} containing:
     *         <ul>
     *           <li>200 OK: Paginated list of inventory items matching criteria</li>
     *           <li>400 Bad Request: Invalid parameters or missing required headers</li>
     *           <li>401 Unauthorized: Invalid or missing authentication headers</li>
     *           <li>500 Internal Server Error: Unexpected server-side errors</li>
     *         </ul>
     * 
     * @throws IllegalArgumentException if pagination parameters are invalid
     * @throws RuntimeException if header validation fails or processing errors occur
     */
    @Operation(
        summary = InventoryApiDocumentation.GetInventory.SUMMARY,
        description = InventoryApiDocumentation.GetInventory.DESCRIPTION,
        operationId = InventoryApiDocumentation.GetInventory.OPERATION_ID
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = InventoryApiDocumentation.Responses.SUCCESS_200_DESCRIPTION,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = GetInventoryResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Successful Response",
                        summary = "Typical successful response with inventory data",
                        externalValue = InventoryApiDocumentation.Examples.SUCCESSFUL_INVENTORY_RESPONSE
                    ),
                    @ExampleObject(
                        name = "Empty Response",
                        summary = "Response when no inventory matches the criteria",
                        externalValue = InventoryApiDocumentation.Examples.EMPTY_INVENTORY_RESPONSE
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = InventoryApiDocumentation.Responses.BAD_REQUEST_400_DESCRIPTION,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = {
                    @ExampleObject(
                        name = "Invalid Page Number",
                        summary = "Negative page number provided",
                        externalValue = InventoryApiDocumentation.Examples.INVALID_PAGE_NUMBER
                    ),
                    @ExampleObject(
                        name = "Invalid Page Size",
                        summary = "Page size exceeds maximum allowed",
                        externalValue = InventoryApiDocumentation.Examples.INVALID_PAGE_SIZE
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = InventoryApiDocumentation.Responses.UNAUTHORIZED_401_DESCRIPTION,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = {
                    @ExampleObject(
                        name = "Authentication Error",
                        summary = "Invalid or missing authentication headers",
                        externalValue = InventoryApiDocumentation.Examples.AUTHENTICATION_ERROR
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = InventoryApiDocumentation.Responses.INTERNAL_ERROR_500_DESCRIPTION,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = {
                    @ExampleObject(
                        name = "Internal Server Error",
                        summary = "Unexpected server-side error occurred",
                        externalValue = InventoryApiDocumentation.Examples.INTERNAL_SERVER_ERROR
                    )
                }
            )
        )
    })
    @GetMapping(path = "/inventory", produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<GetInventoryResponse> getCustomerInventory(
            @Parameter(
                name = "customerNumbers",
                description = "List of customer numbers to query inventory for",
                required = true,
                in = ParameterIn.QUERY,
                schema = @Schema(type = "array", implementation = String.class),
                examples = {
                    @ExampleObject(
                        name = "Customer Numbers Example",
                        summary = "Example list of customer numbers",
                        value = "15182,6887,2-LK2D1Y"
                    )
                }
            )
            @RequestParam(name = "customerNumbers") java.util.List<String> customerNumbers,

            @Parameter(
                name = "serviceType",
                description = InventoryApiDocumentation.Parameters.SERVICE_TYPE_DESCRIPTION,
                example = "Internet"
            )
            @RequestParam(name = "serviceType", required = false) Optional<String> serviceType,

            @Parameter(
                name = "serviceId",
                description = InventoryApiDocumentation.Parameters.SERVICE_ID_DESCRIPTION,
                example = "SVC123456"
            )
            @RequestParam(name = "serviceId", required = false) Optional<String> serviceId) {

        var queryParams = new InventoryQueryParams(
            customerNumbers,
            serviceType,
            serviceId
        );

        logger.debug("Inventory query - {}", queryParams.getQuerySummary());

        return inventoryQueryService.getCustomerInventory(queryParams);
    }
}
