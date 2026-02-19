package com.lumen.inventory.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * PaginationResponse
 *
 * Mirrors the structure of the NaaS PaginationResponse VO for inventory API responses.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginationResponse {
    private Integer pageNumber = null;
    private Integer pageSize = null;
    private Integer totalRecords = null;
}
