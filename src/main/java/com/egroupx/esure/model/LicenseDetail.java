package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDetail {

    private String licenseDate;
    private String licenseCategory;
    private String licenseType;
    private String vehicleRestriction;
}
