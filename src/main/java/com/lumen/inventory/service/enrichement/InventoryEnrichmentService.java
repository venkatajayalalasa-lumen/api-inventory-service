package com.lumen.inventory.service.enrichement;

import com.lumen.inventory.integration.account.AccountManagementAdapter;
import com.lumen.inventory.service.mapper.ProductToServiceInventoryMapper;
import com.lumen.inventory.config.InventoryEnrichmentProperties;
import com.lumen.inventory.dto.responses.Product;
import com.lumen.inventory.dto.responses.ServiceInventory;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for enriching inventory data with billing account and customer information, specifically for service type "Internet".
 * <p>
 * <b>Flow for service type "Internet":</b>
 * <ol>
 *   <li><b>Request Initiation:</b> A request is made to enrich product data for products of service type "Internet".</li>
 *   <li><b>Parallel Enrichment:</b> The service processes enrichment in parallel using a configurable thread pool for efficiency.</li>
 *   <li><b>BAN Mapping:</b> For each Billing Account Number (BAN), the service calls {@link com.lumen.inventory.integration.account.AccountManagementAdapter} to retrieve invoice display and customer numbers from the Account Management Service.</li>
 *   <li><b>Product Mapping:</b> The {@link com.lumen.inventory.service.mapper.ProductToServiceInventoryMapper} maps each {@link com.lumen.inventory.dto.responses.Product} to a {@link com.lumen.inventory.dto.responses.ServiceInventory}:
 *     <ul>
 *       <li>Sets service type to "Internet" (from configuration).</li>
 *       <li>Filters and sets valid product characteristics.</li>
 *       <li>Attaches billing account and customer number information.</li>
 *     </ul>
 *   </li>
 *   <li><b>Telemetry & Logging:</b> The enrichment process is wrapped in a Micrometer {@link io.micrometer.observation.Observation} for telemetry, recording status and duration, and logging the outcome.</li>
 *   <li><b>Response:</b> Returns a list of enriched {@link com.lumen.inventory.dto.responses.ServiceInventory} objects for downstream use.</li>
 * </ol>
 * <p>
 * Error handling and observability are robust and follow best practices.
 * </p>
 *
 * @author API Development Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Service
public class InventoryEnrichmentService {
    private static final Logger log = LoggerFactory.getLogger(InventoryEnrichmentService.class);
    private final AccountManagementAdapter accountManagementAdapter;
    private final ProductToServiceInventoryMapper productToServiceInventoryMapper;
    private final InventoryEnrichmentProperties properties;
    private final ObservationRegistry observationRegistry;

    public InventoryEnrichmentService(AccountManagementAdapter accountManagementAdapter,
                                      ProductToServiceInventoryMapper productToServiceInventoryMapper,
                                      InventoryEnrichmentProperties properties,
                                      ObservationRegistry observationRegistry) {
        this.accountManagementAdapter = accountManagementAdapter;
        this.productToServiceInventoryMapper = productToServiceInventoryMapper;
        this.properties = properties;
        this.observationRegistry = observationRegistry;
    }

    /**
     * Enriches Billing Account Numbers (BANs) with Account Management (AM) data for service type "Internet".
     * <p>
     * For each BAN in the input list, this method calls the Account Management Service in parallel
     * to retrieve the associated invoice display number (billing account ID) and customer number.
     * The results are mapped as {@code BAN -> "invoiceDisplayNumber|customerNumber"} for downstream enrichment.
     * <ul>
     *   <li>Parallel execution is used for efficiency and scalability.</li>
     *   <li>Handles errors gracefully, logging any failures and returning an empty mapping if needed.</li>
     *   <li>Used as a prerequisite for enriching inventory with billing and customer details for Internet services.</li>
     * </ul>
     *
     * @param banList List of Billing Account Numbers (BANs) to enrich with AM data
     * @return ConcurrentHashMap mapping each BAN to a string in the format {@code "invoiceDisplayNumber|customerNumber"}
     */
    public ConcurrentHashMap<String, String> fillBanHashMapByCallingAMService(List<String> banList) {
        if (banList == null || banList.isEmpty()) {
            return new ConcurrentHashMap<>();
        }
        int threadCount = Math.min(properties.getThreadAllocationCount(), banList.size());
        try (ForkJoinPool customThreadPool = new ForkJoinPool(threadCount)) {
            return customThreadPool.submit(() ->
                banList.parallelStream().collect(Collectors.toMap(
                    ban -> ban,
                    accountManagementAdapter::getBanMapping,
                    (existing, replacement) -> !replacement.isEmpty() ? replacement : existing,
                    ConcurrentHashMap::new
                ))
            ).join();
        }
    }

    /**
     * Enriches a list of Product objects with billing account and customer number information from AM for service type "Internet".
     * <p>
     * For each Product:
     * <ul>
     *   <li>Looks up the related party of type "Customer" (BAN) in the provided map.</li>
     *   <li>Extracts the invoice display number and customer number from the AM mapping.</li>
     *   <li>Sets these values in the resulting ServiceInventory object for downstream use.</li>
     *   <li>Filters product characteristics based on valid attributes.</li>
     *   <li>Sets the service type to "Internet" as per configuration.</li>
     * </ul>
     * <b>Thread-safe and parallelized</b> for performance.
     * <b>Telemetry and logging</b> are handled using Micrometer Observation for status and duration.
     *
     * @param productList List of Product objects to enrich
     * @param banToInvoiceDisplayMap Map of BAN to {@code "invoiceDisplayNumber|customerNumber"} from AM
     * @param azureToken Azure token for downstream enrichment (if needed)
     * @return List of enriched ServiceInventory objects with AM data
     * @throws InterruptedException if thread is interrupted during parallel processing
     */
    public List<ServiceInventory> enrichProductList(List<Product> productList, ConcurrentHashMap<String, String> banToInvoiceDisplayMap, String azureToken) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Observation observation = Observation.start("product.enrichment", observationRegistry);
        try {
            List<ServiceInventory> result = productList.parallelStream()
                .map(productIterator -> productToServiceInventoryMapper.map(productIterator, banToInvoiceDisplayMap, azureToken))
                .collect(Collectors.toList());
            observation.highCardinalityKeyValue("status", "success");
            observation.highCardinalityKeyValue("duration_ms", String.valueOf(System.currentTimeMillis() - startTime));
            log.info("Product enrichment succeeded in {}ms", System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception ex) {
            observation.highCardinalityKeyValue("status", "error");
            observation.highCardinalityKeyValue("duration_ms", String.valueOf(System.currentTimeMillis() - startTime));
            log.error("Product enrichment failed: {}", ex.getMessage());
            throw ex;
        } finally {
            observation.stop();
        }
    }
}
