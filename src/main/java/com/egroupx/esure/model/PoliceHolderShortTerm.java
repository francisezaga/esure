package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoliceHolderShortTerm {

    private String heldInsuranceLast39Days;
    private String periodCompCarInsurance;
    private String periodCompNonMotorInsurance;
    private String hasConsent;
    private LicenseDetail licenseDetail;
}
