package com.lumen.inventory.service.internet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.error.enums.ErrorCode;
import com.lumen.error.exception.ProcessingException;
import com.lumen.inventory.dto.InventoryQueryParams;
import com.lumen.inventory.dto.responses.GetInventoryResponse;
import com.lumen.inventory.dto.responses.Product;
import com.lumen.inventory.dto.responses.ServiceInventory;
import com.lumen.inventory.service.enrichement.InventoryEnrichmentService;
import com.lumen.inventory.service.enrichement.LocationEnrichmentService;
import com.lumen.inventory.service.mapper.InventoryMapper;
import com.lumen.snow.service.SNOWRestClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service responsible for handling Internet-specific inventory queries.
 * <p>
 * This class encapsulates all logic related to fetching, enriching, and filtering
 * Internet inventory data, following the Single Responsibility Principle (SRP).
 * It is used by the main InventoryQueryServiceImpl to delegate Internet-related requests.
 * </p>
 *
 * <ul>
 *   <li>Fetches inventory data from ServiceNow (SNOW)</li>
 *   <li>Enriches data with Account Management (AM) and other services</li>
 *   <li>Applies filtering based on serviceId if provided</li>
 * </ul>
 *
 * @author API Development Team
 * @since 2.0.0
 */
@Slf4j
@Service
public class InternetInventoryService {

    @Autowired
    private SNOWRestClient snowRestClient;

    @Autowired
    private InventoryEnrichmentService inventoryEnrichmentService;

    @Value("${snow.naas.detail.inventory.max.pagesize:100}")
    private String naasInventoryDetailMaxPageSize;


    @Autowired
    private LocationEnrichmentService locationEnrichmentService;

    @Value("${naas.product.specification.name.for.SNow:Internet}")
    private String productOfferingNameForSnow;

    /**
     * Retrieves Internet inventory for the given query parameters.
     * <p>
     * This method fetches inventory data from ServiceNow, enriches it, and applies
     * filtering by serviceId if present.
     * </p>
     *
     * @param queryParams Query parameters (customerNumbers, serviceType, serviceId)
     * @return ResponseEntity containing the enriched and filtered inventory response
     */
    public ResponseEntity<GetInventoryResponse> getInventoryInternetList(InventoryQueryParams queryParams) {
        log.info("Processing Internet inventory request");
        List<String> customerNumbers = queryParams.customerNumbers();//Retrieves the list of customer numbers from the query parameters.
        ResponseEntity<GetInventoryResponse> responseEntity;
        try {
            responseEntity = getInternetServiceInformation(String.join(",", customerNumbers));
        } catch (JsonProcessingException | InterruptedException e) {
            log.error("Error occurred while retrieving Internet service information", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GetInventoryResponse());
        }//Tries to fetch and enrich inventory data for the given customers and handles any exceptions that may occur during this process.
        GetInventoryResponse enrichedResponse = responseEntity.getBody();
        GetInventoryResponse inventoryResponse = new GetInventoryResponse();
        inventoryResponse.setInventoryList(new ArrayList<>());
        //Gets the body of the response (the enriched inventory). Initializes a new response object with an empty inventory list.
        if (enrichedResponse != null) {
            inventoryResponse.setInventoryList(enrichedResponse.getInventoryList());
            if (queryParams.serviceId() != null && queryParams.serviceId().isPresent()) {
                List<ServiceInventory> filteredList = inventoryResponse.getInventoryList().stream()
                        .filter(x -> x.getServiceId() != null && x.getServiceId().equalsIgnoreCase(queryParams.serviceId().get()))
                        .collect(Collectors.toList());
                inventoryResponse.setInventoryList(filteredList);
            }
            return new ResponseEntity<>(inventoryResponse, responseEntity.getStatusCode());
            //If the enriched response is not null, copies its inventory list to the new response.
            //If a serviceId is present in the query, filters the inventory list to only include items with a matching service ID.
            //Returns the filtered (or unfiltered) inventory response with the original HTTP status.
        }
        return responseEntity;//If the enriched response was null, returns the original response as is.
    }

    /**
     * Fetches and enriches Internet inventory data from ServiceNow for the given customer numbers.
     * <p>
     * This method performs the following steps:
     * <ul>
     *   <li>Calls ServiceNow API to retrieve raw inventory data</li>
     *   <li>Parses and maps the response to Product objects</li>
     *   <li>Enriches the data with Account Management (AM) information</li>
     *   <li>Builds and returns the final inventory response</li>
     * </ul>
     * </p>
     *
     * @param distinctCustNumbersFromCustAcct Comma-separated customer numbers for ServiceNow query
     * @return ResponseEntity containing the enriched inventory response
     * @throws JsonMappingException if JSON mapping fails
     * @throws JsonProcessingException if JSON processing fails
     * @throws InterruptedException if thread is interrupted during enrichment
     */
    private ResponseEntity<GetInventoryResponse> getInternetServiceInformation(String distinctCustNumbersFromCustAcct)
            throws JsonMappingException, JsonProcessingException, InterruptedException {
        log.info("getInternetServiceInformation   snowRestClient call starting");
        ResponseEntity<String> responseEntity = snowRestClient.getInventoryInternetInformationBasedOnMultipleCustomers(
            //Calls the ServiceNow client to fetch raw inventory data for the given customer numbers, with pagination and page size parameters.
            distinctCustNumbersFromCustAcct, Optional.of(100), Optional.of(20), naasInventoryDetailMaxPageSize, Optional.empty()).block();
        log.info("getInternetServiceInformation   snowRestClient call ended");
        if (responseEntity != null && responseEntity.getBody() != null) {
            List<Product> productList = InventoryMapper.mapJsonToProductList(responseEntity.getBody());//Maps the JSON response body to a list of Product objects.
            List<String> banList = InventoryMapper.getDistinctAlternateNumbersFromSnow(productList);//Extracts a list of distinct alternate numbers (BANs) from the product list.
            log.info("getInternetServiceInformation  fillBanHashMapByCallingAMService multithreading starting");
            //AM Enrichement Process starts here - using multithreading to call AM service for each BAN in parallel and build a map of alternate number to invoice display customer number.
            ConcurrentHashMap<String, String> mapAlternateToInvoiceDisplayCustomerNumber = inventoryEnrichmentService.fillBanHashMapByCallingAMService(banList);
            //Calls the enrichment service to map alternate numbers to invoice display customer numbers, possibly using multithreading.
            log.info("getInternetServiceInformation  fillBanHashMapByCallingAMService multithreading ended");
            List<ServiceInventory> serviceInventoryList = inventoryEnrichmentService.enrichProductList(
                    productList,
                    mapAlternateToInvoiceDisplayCustomerNumber,
                    productOfferingNameForSnow
            );//Enriches the product list with additional data, producing a list of ServiceInventory objects.
            if (serviceInventoryList != null && !serviceInventoryList.isEmpty()) {
                GetInventoryResponse getInventoryResponse = new GetInventoryResponse();
                getInventoryResponse.setInventoryList(serviceInventoryList);
                //Creates a new response object and sets its inventory list.
                locationEnrichmentService.enrichLocationInformation(getInventoryResponse);
                //Further enriches the response with location information. (GLM)
                log.info("getInternetServiceInformation GetInventory   getInternetServiceInformation method ended");
                return new ResponseEntity<>(getInventoryResponse, HttpStatus.OK);
            } else {
                throw new ProcessingException(ErrorCode.NOT_FOUND, "No Internet inventory records found after enrichment for customers: " + distinctCustNumbersFromCustAcct);
                // no data after enrichment.
            }
        }
        throw new ProcessingException(ErrorCode.NOT_FOUND, "No ServiceNow response or empty body for customers: " + distinctCustNumbersFromCustAcct);
        //no data received from ServiceNow.
    }
}
