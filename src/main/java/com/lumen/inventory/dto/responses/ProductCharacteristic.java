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
public class ProductCharacteristic {

	private String name;
    private String valueType;
    private String value;
}
