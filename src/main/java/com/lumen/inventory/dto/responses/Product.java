/**
 * 
 */
package com.lumen.inventory.dto.responses;

import java.util.List;
import lombok.Data;
@Data
public class Product {

	private String id;
    private String description;
    private String name;
    private List<ProductCharacteristic> productCharacteristic;
    private List<ProductRelationship> productRelationship;
    private List<ServiceRef> realizingService;
    private String status;
    // Added for compatibility with legacy enrichment logic
    private List<RelatedParty> relatedParty;
}
