package com.lumen.inventory.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceRef {
  @SerializedName("id")
  private String id = null;

  @SerializedName("href")
  private String href = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("@baseType")
  private String baseType = null;

  @SerializedName("@schemaLocation")
  private String schemaLocation = null;

  @SerializedName("@type")
  private String type = null;

  @SerializedName("@referredType")
  private String referredType = null;

}