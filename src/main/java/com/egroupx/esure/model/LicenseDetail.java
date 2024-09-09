package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDetail {

    @JsonAlias({"licenseDate","license-date"})
    private String licenseDate;
    @JsonAlias({"licenseCategory","license-category"})
    private String licenseCategory;
    @JsonAlias({"licenseType","license-type"})
    private String licenseType;
    @JsonAlias({"vehicleRestriction","vehicle-restriction"})
    private String vehicleRestriction;
}
