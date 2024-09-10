package com.egroupx.esure.dto.fsp_qoute.personal_details;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDetail {

    @JsonAlias("licenseDate")
    @JsonProperty("license-date")
    private String licenseDate;
    @JsonAlias("licenseCategory")
    @JsonProperty("license-category")
    private String licenseCategory;
    @JsonAlias("licenseType")
    @JsonProperty("license-type")
    private String licenseType;
    @JsonAlias("vehicleRestriction")
    @JsonProperty("vehicle-restriction")
    private String vehicleRestriction;
}
