package com.lumen.inventory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "naas.inventory")
public class InventoryEnrichmentProperties {
    private int threadAllocationCount = 10;
    private String validAttributeList = "";
    private String productSpecificationNameForSNow = "Internet";

    public int getThreadAllocationCount() {
        return threadAllocationCount;
    }

    public void setThreadAllocationCount(int threadAllocationCount) {
        this.threadAllocationCount = threadAllocationCount;
    }

    public String getValidAttributeList() {
        return validAttributeList;
    }

    public void setValidAttributeList(String validAttributeList) {
        this.validAttributeList = validAttributeList;
    }

    public String getProductSpecificationNameForSNow() {
        return productSpecificationNameForSNow;
    }

    public void setProductSpecificationNameForSNow(String productSpecificationNameForSNow) {
        this.productSpecificationNameForSNow = productSpecificationNameForSNow;
    }
}
