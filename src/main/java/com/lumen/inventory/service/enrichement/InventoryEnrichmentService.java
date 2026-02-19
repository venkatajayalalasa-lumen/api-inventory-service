package com.lumen.inventory.service.enrichement;

import com.lumen.inventory.dto.responses.ServiceInventory;
import com.lumen.account.management.service.AccountManagementService;
import com.lumen.inventory.dto.responses.Product;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Service for enriching inventory data with billing account and customer information.
 * <p>
 * This service provides methods to:
 * <ul>
 *   <li>Map Billing Account Numbers (BANs) to invoice display and customer numbers by calling Account Management Service</li>
 *   <li>Enrich product lists with billing and customer information for downstream use</li>
 * </ul>
 * <p>
 * Enrichment is performed in parallel for efficiency. Error handling and logging are robust.
 * </p>
 *
 * @author API Development Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Service
public class InventoryEnrichmentService {
    @Value("${naas.inventory.thread.allocation.count:10}")
    private String naasInventoryThreadAllocationCount;
    @Value("${naas.inventoryapi.valid.attribute.list:}")
    private String naasValidAttributes;
    @Value("${naas.product.specification.name.for.SNow:Internet}")
    private String productOfferingNameForSnow;

    private final AccountManagementService accountManagementService;

    public InventoryEnrichmentService(AccountManagementService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }

    /**
     * Enriches Billing Account Numbers (BANs) with Account Management (AM) data.
     * <p>
     * For each BAN in the input list, this method calls the Account Management Service in parallel
     * to retrieve the associated invoice display number (billing account ID) and customer number.
     * The results are mapped as {@code BAN -> "invoiceDisplayNumber|customerNumber"} for downstream enrichment.
     * <ul>
     *   <li>Parallel execution is used for efficiency and scalability.</li>
     *   <li>Handles errors gracefully, logging any failures and returning an empty mapping if needed.</li>
     *   <li>Used as a prerequisite for enriching inventory with billing and customer details.</li>
     * </ul>
     *
     * @param banList List of Billing Account Numbers (BANs) to enrich with AM data
     * @return ConcurrentHashMap mapping each BAN to a string in the format {@code "invoiceDisplayNumber|customerNumber"}
     */
    public ConcurrentHashMap<String, String> fillBanHashMapByCallingAMService(List<String> banList) {
        if (banList == null || banList.isEmpty()) {
            return new ConcurrentHashMap<>();
        }
        int threadCount = Math.min(Integer.parseInt(naasInventoryThreadAllocationCount), banList.size());
        try (ForkJoinPool customThreadPool = new ForkJoinPool(threadCount)) {
            return customThreadPool.submit(() ->
                banList.parallelStream().collect(Collectors.toMap(
                    ban -> ban,
                    ban -> {
                        try {
                            // Prepare headers with x-customer-number as BAN
                            Map<String, String> headers = new HashMap<>();
                            headers.put("x-customer-number", ban);
                            // Call AccountManagementService to get billing accounts for this BAN
                            var response = accountManagementService.getBillingAccounts(
                                headers,
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty()
                            );
                            if (response != null && response.getBillingAccounts() != null && !response.getBillingAccounts().isEmpty()) {
                                var billingAccount = response.getBillingAccounts().get(0);
                                String invoiceDisplayNumber = billingAccount.getId();
                                String customerNumber = response.getCustomerNumber();
                                return (invoiceDisplayNumber != null ? invoiceDisplayNumber : "") + "|" + (customerNumber != null ? customerNumber : "");
                            }
                            return "|";
                        } catch (Exception e) {
                            org.slf4j.LoggerFactory.getLogger(InventoryEnrichmentService.class)
                                .error("fillBanHashMapByCallingAMService: Error processing BAN {}: {}", ban, e.getMessage(), e);
                            return "|";
                        }
                    },
                    (existing, replacement) -> !replacement.isEmpty() ? replacement : existing,
                    ConcurrentHashMap::new
                ))
            ).join();
        }
    }

    /**
     * Enriches a list of Product objects with billing account and customer number information from AM.
     * <p>
     * For each Product:
     * <ul>
     *   <li>Looks up the related party of type "Customer" (BAN) in the provided map.</li>
     *   <li>Extracts the invoice display number and customer number from the AM mapping.</li>
     *   <li>Sets these values in the resulting ServiceInventory object for downstream use.</li>
     *   <li>Filters product characteristics based on valid attributes.</li>
     * </ul>
     * <b>Thread-safe and parallelized</b> for performance.
     *
     * @param productList List of Product objects to enrich
     * @param banToInvoiceDisplayMap Map of BAN to {@code "invoiceDisplayNumber|customerNumber"} from AM
     * @param azureToken Azure token for downstream enrichment (if needed)
     * @return List of enriched ServiceInventory objects with AM data
     * @throws InterruptedException if thread is interrupted during parallel processing
     */
    public List<ServiceInventory> enrichProductList(List<Product> productList, ConcurrentHashMap<String, String> banToInvoiceDisplayMap, String azureToken) throws InterruptedException {
        // Use parallelStream for improved performance and thread safety
        return productList.parallelStream()
            .map(productIterator -> {
                Map<String, String> newContextMDCMap = MDC.getCopyOfContextMap();
                if (newContextMDCMap != null && !newContextMDCMap.isEmpty() && newContextMDCMap.containsKey("trackingid")) {
                    MDC.setContextMap(newContextMDCMap);
                }
                return fillServiceInventory(productIterator, banToInvoiceDisplayMap, azureToken);
            })
            .collect(Collectors.toList());
    }

    /**
     * Builds a ServiceInventory object for a given Product, enriching it with AM billing account and customer number.
     * <p>
     * For the given Product:
     * <ul>
     *   <li>Finds the related party of type "Customer" (BAN) and looks up the AM mapping.</li>
     *   <li>Splits the AM mapping to extract invoice display number and customer number.</li>
     *   <li>Sets these values in the ServiceInventory object.</li>
     *   <li>Filters and sets valid product characteristics.</li>
     * </ul>
     *
     * @param product The Product to enrich
     * @param banToInvoiceDisplayMap Map of BAN to {@code "invoiceDisplayNumber|customerNumber"} from AM
     * @param azureToken Azure token for downstream enrichment (if needed)
     * @return Enriched ServiceInventory object with AM data
     */
    private ServiceInventory fillServiceInventory(Product product, ConcurrentHashMap<String, String> banToInvoiceDisplayMap, String azureToken) {
        ServiceInventory inventory = new ServiceInventory();
        inventory.setServiceId(product.getId());
        inventory.setServiceType(productOfferingNameForSnow);
        inventory.setStatus(product.getStatus());
        if (product.getProductCharacteristic() != null) {
            List<String> validAttributes = Arrays.asList(naasValidAttributes.split(","));
            List<com.lumen.inventory.dto.responses.ProductCharacteristic> filteredCharacteristics = product.getProductCharacteristic().stream()
                .filter(c -> validAttributes.contains(c.getName()))
                .collect(Collectors.toList());
            inventory.setProductCharacteristic(filteredCharacteristics);
        }
        if (product.getRelatedParty() != null) {
            for (var party : product.getRelatedParty()) {
                if (party.getReferredType() != null && party.getReferredType().equalsIgnoreCase("Customer")) {
                    String value = banToInvoiceDisplayMap.get(party.getId());
                    if (org.springframework.util.StringUtils.hasText(value)) {
                        String[] parts = value.split("\\|", 2);
                        String invoiceDisplayNumber = parts.length > 0 ? parts[0] : "";
                        String customerNumber = parts.length > 1 ? parts[1] : "";
                        com.lumen.inventory.dto.responses.BillingAccountResponse billingAccount = new com.lumen.inventory.dto.responses.BillingAccountResponse();
                        billingAccount.setId(invoiceDisplayNumber);
                        inventory.setBillingAccount(billingAccount);
                        inventory.setCustomerNumber(customerNumber);
                    }
                    break;
                }
            }
        }
        return inventory;
    }
}
