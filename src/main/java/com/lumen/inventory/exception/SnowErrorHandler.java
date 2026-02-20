package com.lumen.inventory.exception;

import com.lumen.error.enums.ErrorCode;
import com.lumen.error.exception.ProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class SnowErrorHandler {

    private final JsonMapper jsonMapper;

    public ProcessingException handleWebClientError(WebClientResponseException ex) {
        String errorBody = ex.getResponseBodyAsString();
        int statusCode = ex.getStatusCode().value();
        String extractedMessage = extractErrorMessage(errorBody);
        return switch (statusCode) {
            case 400 -> new ProcessingException(ErrorCode.BAD_REQUEST, extractedMessage);
            case 404 -> new ProcessingException(ErrorCode.NOT_FOUND, "No Record Found for the provided information");
            case 401 -> new ProcessingException(ErrorCode.UNAUTHORIZED, extractedMessage);
            case 500 -> new ProcessingException(ErrorCode.INTERNAL_SERVER_ERROR, "ServiceNow API internal server error");
            case 503 -> new ProcessingException(ErrorCode.SERVICE_UNAVAILABLE, "ServiceNow API service unavailable");
            default -> new ProcessingException("Exception Occurred: " + extractedMessage);
        };
    }

    private String extractErrorMessage(String errorBody) {
        log.error("Error response from ServiceNow API: {}", errorBody);
        try {
            JsonNode root = jsonMapper.readTree(errorBody);
            JsonNode exceptionNode = root.path("error");
            if (!exceptionNode.isMissingNode()) return exceptionNode.toString();
            else return "Unable to process the request. Please try again later.";
        } catch (Exception e) {
            log.warn("Failed to parse error body: {}", errorBody, e);
            return "Unable to process the request. Please try again later.";
        }
    }
}
