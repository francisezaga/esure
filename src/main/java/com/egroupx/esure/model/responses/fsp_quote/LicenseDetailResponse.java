package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDetailResponse {

    @JsonAlias("license-date")
    private String licenseDate;
    @JsonAlias("license-category")
    private String licenseCategory;
    @JsonAlias("license-type")
    private String licenseType;
    @JsonAlias("vehicle-restriction")
    private String vehicleRestriction;
}
