package com.lumen.inventory.service.enrichement;

import com.lumen.inventory.dto.responses.Address;
import com.lumen.inventory.dto.responses.GetInventoryResponse;
import com.lumen.inventory.dto.responses.ServiceInventory;
import com.lumen.glm.service.GLMRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationEnrichmentService {
    private static final Logger log = LoggerFactory.getLogger(LocationEnrichmentService.class);


    private final GLMRestClient glmRestClient;

    public LocationEnrichmentService(GLMRestClient glmRestClient) {
        this.glmRestClient = glmRestClient;
    }

    public void enrichLocationInformation(GetInventoryResponse getInventoryResponse) {
        log.info("In InventoryService to get LocationInformation for InventoryData");
        log.info("GLM started");
        log.error("GetInventory  getLocationInformation started");
        try {
            List<ServiceInventory> serviceInventoryList = getInventoryResponse.getInventoryList();
            if (serviceInventoryList != null && !serviceInventoryList.isEmpty()) {
                List<String> glmList = serviceInventoryList.stream()
                        .filter(y -> y.getLocation() != null)
                        .map(x -> x.getLocation().getMasterSiteid())
                        .distinct()
                        .collect(Collectors.toList());
                log.info("GLM id count {}", glmList.size());
                log.info("glm List {}", glmList);
                if (!glmList.isEmpty()) {
                    log.info("GetInventory  getLocationInformation thread started");
                    List<com.lumen.glm.dto.ServiceLocation> glmResponses = glmRestClient.getLocationInformationForList(glmList).block();
                    List<com.lumen.glm.dto.ServiceLocation> siteLocationList = new ArrayList<>();
                    if (glmResponses != null) {
                        siteLocationList.addAll(glmResponses);
                    }
                    log.info("GLM Processing Completed");
                    log.info("Size of GLM Response returned {}", siteLocationList.size());
                    log.info("Size of service Inventory {}", serviceInventoryList.size());
                    for (ServiceInventory serviceInventoryIterator : serviceInventoryList) {
                        if (serviceInventoryIterator.getLocation() != null && StringUtils.hasLength(serviceInventoryIterator.getLocation().getMasterSiteid())) {
                            List<com.lumen.glm.dto.ServiceLocation> filteredSiteLocationList = siteLocationList.stream()
                                    .filter(x -> x.getMasterSiteId().equalsIgnoreCase(serviceInventoryIterator.getLocation().getMasterSiteid()))
                                    .collect(Collectors.toList());
                            log.info("Checking for MasterSiteId if present {} is present {}", serviceInventoryIterator.getLocation().getMasterSiteid(), filteredSiteLocationList.isEmpty());
                            if (!filteredSiteLocationList.isEmpty()) {
                                com.lumen.glm.dto.ServiceLocation siteLocation = filteredSiteLocationList.get(0);
                                Address address = new Address();
                                address.setMasterSiteid(siteLocation.getMasterSiteId());
                                // Enrich with as many fields as possible from ServiceLocation
                                address.setStreetAddress(siteLocation.getDescription());
                                address.setStateOrProvince(siteLocation.getSiteStatusType());
                                address.setPostcode(siteLocation.getUSZip4());
                                // Example: enrich with nested AddressLine1 if available
                                if (siteLocation.getAddressLine1() != null) {
                                    address.setAddressLine1(siteLocation.getAddressLine1().getAddressBlock1());
                                }
                                // Example: enrich with nested Address if available
                                if (siteLocation.getAddresses() != null && !siteLocation.getAddresses().isEmpty()) {
                                    address.setAddressBlock2(siteLocation.getAddresses().get(0).getAddressBlock2());
                                }
                                // Example: enrich with building info if available
                                if (siteLocation.getBuilding() != null) {
                                    address.setBuildingName(siteLocation.getBuilding().getBuildingName());
                                }
                                // Add more mappings as needed for your Address DTO
                                // (e.g., country, city, extension, etc.)
                                serviceInventoryIterator.setLocation(address);
                            }
                        }
                    }
                }
            }
            log.info("GLM Ended");
            log.error("GetInventory  getLocationInformation ended");
        } catch (Exception e) {
            log.error("Exception occurred while setting location object " + e.getMessage());
        }
    }
}
