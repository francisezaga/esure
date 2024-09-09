package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDetailResponse {

    private String licenseDate;
    private String licenseCategory;
    private String licenseType;
    private String vehicleRestriction;
}
