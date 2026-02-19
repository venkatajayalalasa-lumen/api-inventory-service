/**
 * 
 */
package com.lumen.inventory.dto.responses;

import java.util.List;

import lombok.Data;

@Data
public class SubLocationResponse {

	
	private String serviceLocation;
    private String buildingName;
    private String levelType;
    private String levelNumber;
    private String privateStreetNumber;
    private String privateStreetName;
    private List<SubUnit> subUnit;
}
