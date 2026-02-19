/**
 * 
 */
package com.lumen.inventory.dto.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author AB54018
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
	
	 private String masterSiteid;
	 private String streetAddress;
	 private String city;
	 private String stateOrProvince;
	 private String locality;
	 private String country;
	 private String postcode;
	 private String postcodeExtension;
}
