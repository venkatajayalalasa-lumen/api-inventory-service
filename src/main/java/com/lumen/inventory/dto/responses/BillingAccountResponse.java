package com.lumen.inventory.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class BillingAccountResponse {

	private String id;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String financeAccountNumber;
    private String name;
/**
 * Data Transfer Object for Billing Account Response.
 * <p>Represents the response structure for billing account queries.</p>
 */
}

