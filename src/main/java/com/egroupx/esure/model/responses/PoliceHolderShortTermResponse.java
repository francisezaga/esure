package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoliceHolderShortTermResponse {

    private String heldInsuranceLast39Days;
    private String periodCompCarInsurance;
    private String periodCompNonMotorInsurance;
    private String hasConsent;
    private LicenseDetailResponse licenseDetail;
}
