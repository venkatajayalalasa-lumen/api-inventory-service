package com.lumen.inventory.dto.responses;

import lombok.Data;

@Data
public class RelatedParty {
    private String id;
    private String href;
    private String name;
    private String role;
    private String baseType;
    private String schemaLocation;
    private String type;
    private String referredType;
}
