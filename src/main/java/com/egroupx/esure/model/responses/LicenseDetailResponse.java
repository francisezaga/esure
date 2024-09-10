package com.egroupx.esure.model.responses;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDetailResponse {

    @JsonProperty("license-date")
    private String licenseDate;
    @JsonProperty("license-category")
    private String licenseCategory;
    @JsonProperty("license-type")
    private String licenseType;
    @JsonProperty("vehicle-restriction")
    private String vehicleRestriction;
}
