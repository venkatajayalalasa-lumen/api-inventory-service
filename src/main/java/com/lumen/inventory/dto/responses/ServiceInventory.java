package com.lumen.inventory.dto.responses;

import lombok.Data;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object for Product Inventory Item.
 * <p>Represents a single inventory item in inventory queries.</p>
 * 
 * <p>This class follows the TMF637 Product Inventory Management API standard
 * and aligns with the response structure from ServiceManagementV4.java</p>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceInventory {

	
	 private String serviceId;
	 private String inventoryId;
     private String serviceName;
     private String productCode;
     private String productName;
     private String serviceType;
     private String status;
     private BillingAccountResponse billingAccount;
     private Address location;
     private List<ProductCharacteristic> productCharacteristic;
     private SubLocationResponse subLocation;
     private LocationProfile locationProfile;
     private List<ProductPrice> productPrice;
     private Product product;
     private String customerNumber;
/**
 * Data Transfer Object for Service Inventory Item.
 * <p>Represents a single inventory item in inventory queries.</p>
 */
}
