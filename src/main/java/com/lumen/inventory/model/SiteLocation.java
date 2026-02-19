// package com.lumen.inventory.model;

// import java.util.ArrayList;

// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import com.fasterxml.jackson.annotation.JsonProperty;

// import lombok.Data;
// @Data
// @JsonIgnoreProperties(ignoreUnknown = true)
// public class SiteLocation {

// 	@JsonProperty("ErrorMessage") 
//     private String errorMessage;
//     @JsonProperty("CLLIId") 
//     private String cLLIId;
//     @JsonProperty("HasWorkItems") 
//     private boolean hasWorkItems;
//     @JsonProperty("MasterSiteId") 
//     private String masterSiteId;
//     @JsonProperty("SiteStatusType") 
//     private String siteStatusType;
//     @JsonProperty("SiteStatusTypeId") 
//     private String siteStatusTypeId;
//     @JsonProperty("SiteType") 
//     private String siteType;
//     @JsonProperty("SiteTypeId") 
//     private String siteTypeId;
//     @JsonProperty("DemarcDateModified") 
//     private String demarcDateModified;
//     @JsonProperty("DemarcModifiedById") 
//     private String demarcModifiedById;
//     @JsonProperty("IsDemarcCertified") 
//     private boolean isDemarcCertified;
//     @JsonProperty("AddressLine1") 
//     private AddressLine1 addressLine1;
//     @JsonProperty("Addresses") 
//     private ArrayList<Address> addresses;
//     @JsonProperty("BillingGeoCode") 
//     private String billingGeoCode;
//     @JsonProperty("Building") 
//     private Building building;
//     @JsonProperty("BuildingAccess") 
//     private BuildingAccess buildingAccess;
//     @JsonProperty("BuildingExtension") 
//     private String buildingExtension;
//     @JsonProperty("BuildingProgram") 
//     private ArrayList<BuildingProgram> buildingProgram;
//     @JsonProperty("CloneSiteId") 
//     private String cloneSiteId;
//     @JsonProperty("ColoAccessTypes") 
//     private ArrayList<ColoAccessType> coloAccessTypes;
//     @JsonProperty("CompetitiveEnvironment") 
//     private CompetitiveEnvironment competitiveEnvironment;
//     @JsonProperty("DemarcCode") 
//     private String demarcCode;
//     @JsonProperty("DemarcName") 
//     private String demarcName;
//     @JsonProperty("DispatchAreaId") 
//     private String dispatchAreaId;
//     @JsonProperty("DispatchAreaName") 
//     private String dispatchAreaName;
//     @JsonProperty("DispatchGatewayClli") 
//     private String dispatchGatewayClli;
//     @JsonProperty("E911CommunityName") 
//     private String e911CommunityName;
//     @JsonProperty("ECNPolygonMembership") 
//     private ArrayList<ECNPolygonMembership> eCNPolygonMembership;
//     @JsonProperty("ExternalReferenceRecords") 
//     private ArrayList<ExternalReferenceRecord> externalReferenceRecords;
//     @JsonProperty("FtRevenueMarketCityState") 
//     private String ftRevenueMarketCityState;
//     @JsonProperty("FtRevenueMarketId") 
//     private long ftRevenueMarketId;
//     @JsonProperty("FtRevenueMarketName") 
//     private String ftRevenueMarketName;
//     @JsonProperty("FtRevenueMarketPCATId") 
//     private String ftRevenueMarketPCATId;
//     @JsonProperty("HCoordinate") 
//     private String hCoordinate;
//     @JsonProperty("InventorySystems") 
//     private ArrayList<InventorySystem> inventorySystems;
//     @JsonProperty("IsDedicatedTenant") 
//     private boolean isDedicatedTenant;
//     @JsonProperty("IsGeocodeManualOverride") 
//     private boolean isGeocodeManualOverride;
//     @JsonProperty("LATA") 
//     private String lATA;
//     @JsonProperty("LataName") 
//     private String lataName;
//     @JsonProperty("Latitude") 
//     private String latitude;
//     @JsonProperty("Longitude") 
//     private String longitude;
//     @JsonProperty("MarketCityState") 
//     private String marketCityState;
//     @JsonProperty("MarketId") 
//     private long marketId;
//     @JsonProperty("MarketName") 
//     private String marketName;
//     @JsonProperty("MarketPCATId") 
//     private String marketPCATId;
//     @JsonProperty("MarketProfitCenterId") 
//     private String marketProfitCenterId;
//     @JsonProperty("MarketState") 
//     private String marketState;
//     @JsonProperty("NPA") 
//     private String nPA;
//     @JsonProperty("NXX") 
//     private String nXX;
//     @JsonProperty("NetworkEntities") 
//     private ArrayList<NetworkEntity> networkEntities;
//     @JsonProperty("Ocn") 
//     private String ocn;
//     @JsonProperty("OcnName") 
//     private String ocnName;
//     @JsonProperty("OcnType") 
//     private String ocnType;
//     @JsonProperty("OffNetOption") 
//     private String offNetOption;
//     @JsonProperty("OffNetOptionTypeId") 
//     private String offNetOptionTypeId;
//     @JsonProperty("OverBuildProgram") 
//     private ArrayList<OverBuildProgram> overBuildProgram;
//     @JsonProperty("PreApprovedTypes") 
//     private ArrayList<PreApprovedType> preApprovedTypes;
//     @JsonProperty("PreapprovedConstructionThreshold") 
//     private double preapprovedConstructionThreshold;
//     @JsonProperty("PrecisionCode") 
//     private String precisionCode;
//     @JsonProperty("PrecisionName") 
//     private String precisionName;
//     @JsonProperty("PreferredSiteCode") 
//     private String preferredSiteCode;
//     @JsonProperty("PricingAreaEthernetCapable") 
//     private boolean pricingAreaEthernetCapable;
//     @JsonProperty("PricingAreaId") 
//     private String pricingAreaId;
//     @JsonProperty("PricingAreaName") 
//     private String pricingAreaName;
//     @JsonProperty("PricingAreaTdmCapable") 
//     private boolean pricingAreaTdmCapable;
//     @JsonProperty("RateCenter") 
//     private RateCenter rateCenter;
//     @JsonProperty("RedirectedToID") 
//     private String redirectedToID;
//     @JsonProperty("Region") 
//     private String region;
//     @JsonProperty("RegionName") 
//     private String regionName;
//     @JsonProperty("ReleaseToLOACFA") 
//     private String releaseToLOACFA;
//     @JsonProperty("RequestedIdRedirected") 
//     private boolean requestedIdRedirected;
//     @JsonProperty("RiserManagementCompany") 
//     private String riserManagementCompany;
//     @JsonProperty("RiserManagementStatus") 
//     private String riserManagementStatus;
//     @JsonProperty("ServedBy") 
//     private String servedBy;
//     @JsonProperty("Serves") 
//     private ArrayList<String> serves;
//     @JsonProperty("ServesMultipleCustomers") 
//     private String servesMultipleCustomers;
//     @JsonProperty("ServiceLocations") 
//     private ArrayList<ServiceLocation> serviceLocations;
//     @JsonProperty("ServingAreaId") 
//     private String servingAreaId;
//     @JsonProperty("ServingAreaName") 
//     private String servingAreaName;
//     @JsonProperty("SiteCOLO") 
//     private ArrayList<SiteCOLO> siteCOLO;
//     @JsonProperty("SiteCapabilityTypeData") 
//     private ArrayList<SiteCapabilityTypeData> siteCapabilityTypeData;
//     @JsonProperty("SiteCapabilityTypes") 
//     private ArrayList<SiteCapabilityType> siteCapabilityTypes;
//     @JsonProperty("SiteCodes") 
//     private ArrayList<SiteCode> siteCodes;
//     @JsonProperty("SiteCreatedBy") 
//     private String siteCreatedBy;
//     @JsonProperty("SiteCreationDate") 
//     private String siteCreationDate;
//     @JsonProperty("SiteHomingGateways") 
//     private ArrayList<SiteHomingGateway> siteHomingGateways;
//     @JsonProperty("SiteIpEdges") 
//     private ArrayList<SiteIpEdge> siteIpEdges;
//     @JsonProperty("SiteIsInServiceAreaType") 
//     private String siteIsInServiceAreaType;
//     @JsonProperty("SiteIsInServiceAreaTypeId") 
//     private String siteIsInServiceAreaTypeId;
//     @JsonProperty("SiteIsOnNet") 
//     private boolean siteIsOnNet;
//     @JsonProperty("SiteLateralDistance") 
//     private SiteLateralDistance siteLateralDistance;
//     @JsonProperty("SiteLeastCostRoute") 
//     private SiteLeastCostRoute siteLeastCostRoute;
//     @JsonProperty("SiteLeastCostRouteByNetwork") 
//     private ArrayList<SiteLeastCostRouteByNetwork> siteLeastCostRouteByNetwork;
//     @JsonProperty("SiteMSAG") 
//     private SiteMSAG siteMSAG;
//     @JsonProperty("SiteName") 
//     private String siteName;
//     @JsonProperty("SiteNetworkData") 
//     private SiteNetworkData siteNetworkData;
//     @JsonProperty("SiteNetworkDistance") 
//     private SiteNetworkDistance siteNetworkDistance;
//     @JsonProperty("SiteNotes") 
//     private ArrayList<SiteNote> siteNotes;
//     @JsonProperty("SiteOnNetDate") 
//     private String siteOnNetDate;
//     @JsonProperty("SiteTenant") 
//     private SiteTenant siteTenant;
//     @JsonProperty("SiteUsages") 
//     private ArrayList<SiteUsage> siteUsages;
//     @JsonProperty("SubRegion") 
//     private String subRegion;
//     @JsonProperty("TWTCTypes") 
//     private ArrayList<TWTCType> tWTCTypes;
//     @JsonProperty("TimeZoneData") 
//     private TimeZoneData timeZoneData;
//     @JsonProperty("VCoordinate") 
//     private String vCoordinate;
//     @JsonProperty("WireCenter100GMetro3") 
//     private String wireCenter100GMetro3;
//     @JsonProperty("WireCenterCLLI") 
//     private String wireCenterCLLI;
//     @JsonProperty("WireCenterEthernetDisclosed") 
//     private String wireCenterEthernetDisclosed;
//     @JsonProperty("WireCenterIslandMarket") 
//     private String wireCenterIslandMarket;
//     @JsonProperty("WireCenterLumenNetwork") 
//     private String wireCenterLumenNetwork;
//     @JsonProperty("WireCenterNNIInterconnect") 
//     private String wireCenterNNIInterconnect;
//     @JsonProperty("WireCenterOperatorName") 
//     private String wireCenterOperatorName;
//     @JsonProperty("WireCenterP2PInterconnect") 
//     private String wireCenterP2PInterconnect;
//     @JsonProperty("WireCenterSpecName") 
//     private String wireCenterSpecName;
//     @JsonProperty("WireCenterTransportStatus") 
//     private String wireCenterTransportStatus;
    
// }
