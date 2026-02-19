/**
 * 
 */
package com.lumen.inventory.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationProfile {

	 private boolean dataCenter;
     private boolean naasEnabled;
     private String market;
     //private RelatedPartyRef relatedParty;
}
