package com.lumen.inventory.service.mapper;

import com.lumen.inventory.dto.responses.Product;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryMapper {

    /**
     * Maps a JSON string to a List of Product objects.
     * @param json JSON string representing a list of products
     * @return List of Product objects
     */
    public static List<com.lumen.inventory.dto.responses.Product> mapJsonToProductList(String json) throws com.fasterxml.jackson.core.JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.core.type.TypeReference<java.util.List<com.lumen.inventory.dto.responses.Product>> typeRef = new com.fasterxml.jackson.core.type.TypeReference<java.util.List<com.lumen.inventory.dto.responses.Product>>() {};
        return objectMapper.readValue(json, typeRef);
    }

    public static List<String> getDistinctAlternateNumbersFromSnow(List<Product> productList) {
        return productList.stream()
                .filter(x -> x.getRelatedParty() != null)
                .flatMap(product -> product.getRelatedParty().stream())
                .filter(party -> org.springframework.util.StringUtils.hasLength(party.getReferredType()) &&
                                party.getReferredType().equalsIgnoreCase("Customer"))
                .map(party -> party.getId())
                .distinct()
                .collect(Collectors.toList());
                
    }
}