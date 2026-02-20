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
            List<ServiceInventory> serviceInventoryList = getInventoryResponse.getInventoryList();//Retrieves the list of ServiceInventory objects from the response.
            if (serviceInventoryList != null && !serviceInventoryList.isEmpty()) {
                List<String> glmList = serviceInventoryList.stream()
                        .filter(y -> y.getLocation() != null)
                        .map(x -> x.getLocation().getMasterSiteid())
                        .distinct()
                        .collect(Collectors.toList());//Collects a distinct list of all masterSiteid values from inventory items that have a location.
                log.info("GLM id count {}", glmList.size());
                log.info("glm List {}", glmList);
                if (!glmList.isEmpty()) {
                    log.info("GetInventory  getLocationInformation thread started");
                    List<com.lumen.glm.dto.ServiceLocation> glmResponses = glmRestClient.getLocationInformationForList(glmList).block();
                    //Calls the GLM REST client to fetch location information for all IDs (blocking call).
                    List<com.lumen.glm.dto.ServiceLocation> siteLocationList = new ArrayList<>();
                    if (glmResponses != null) {
                        siteLocationList.addAll(glmResponses);
                    }//Initializes a list for site locations and adds all responses if not null.
                    log.info("GLM Processing Completed");
                    log.info("Size of GLM Response returned {}", siteLocationList.size());
                    log.info("Size of service Inventory {}", serviceInventoryList.size());
                    for (ServiceInventory serviceInventoryIterator : serviceInventoryList) {
                        if (serviceInventoryIterator.getLocation() != null && StringUtils.hasLength(serviceInventoryIterator.getLocation().getMasterSiteid())) {
                            //Iterates over each inventory item, checking if it has a location and a non-empty masterSiteid.
                            List<com.lumen.glm.dto.ServiceLocation> filteredSiteLocationList = siteLocationList.stream()
                                    .filter(x -> x.getMasterSiteId().equalsIgnoreCase(serviceInventoryIterator.getLocation().getMasterSiteid()))
                                    .collect(Collectors.toList());
                                    //Filters the site locations to those matching the current inventory item's masterSiteid.
                            log.info("Checking for MasterSiteId if present {} is present {}", serviceInventoryIterator.getLocation().getMasterSiteid(), filteredSiteLocationList.isEmpty());
                            if (!filteredSiteLocationList.isEmpty()) {
                                com.lumen.glm.dto.ServiceLocation siteLocation = filteredSiteLocationList.get(0);
                                Address address = new Address();
                                address.setMasterSiteid(siteLocation.getMasterSiteId());
                                //Logs whether a matching site location was found. If found, gets the first match and creates a new Address object, setting its masterSiteid.
                                if (siteLocation.getAddressLine1() != null) {
                                    address.setStreetAddress(siteLocation.getAddressLine1().getAddressLine1and2Combined());
                                    address.setCity(siteLocation.getAddressLine1().getCity());
                                    address.setStateOrProvince(siteLocation.getAddressLine1().getStateId());
                                    address.setCountry(siteLocation.getAddressLine1().getAddressBlock3());
                                    address.setPostcode(siteLocation.getAddressLine1().getPostalCode());
                                }
                                serviceInventoryIterator.setLocation(address);
                                //If the site location has address details, sets them on the new Address object, then updates the inventory item's location with this enriched address.
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
