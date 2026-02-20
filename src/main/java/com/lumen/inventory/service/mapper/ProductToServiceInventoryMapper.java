package com.lumen.inventory.service.mapper;

import com.lumen.inventory.dto.responses.Product;
import com.lumen.inventory.dto.responses.ServiceInventory;
import com.lumen.inventory.config.InventoryEnrichmentProperties;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Maps Product to ServiceInventory, applying enrichment.
 */
@Component
public class ProductToServiceInventoryMapper {
    private final InventoryEnrichmentProperties properties;

    public ProductToServiceInventoryMapper(InventoryEnrichmentProperties properties) {
        this.properties = properties;
    }

    public ServiceInventory map(Product product, ConcurrentHashMap<String, String> banToInvoiceDisplayMap, String azureToken) {
        ServiceInventory inventory = new ServiceInventory();
        inventory.setServiceId(product.getId());
        inventory.setServiceType(properties.getProductSpecificationNameForSNow());
        inventory.setStatus(product.getStatus());
        if (product.getProductCharacteristic() != null) {
            List<String> validAttributes = Arrays.asList(properties.getValidAttributeList().split(","));
            var filteredCharacteristics = product.getProductCharacteristic().stream()
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
