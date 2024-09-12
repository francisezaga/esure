package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PHLicenseDetail {
    private Long id;
    private Long fspQuoteRefId;
    private Long fspPhRefId;
    private LocalDate licenseDate;
    private String licenseCategory;
    private String licenseType;
    private String vehicleRestriction;
}
