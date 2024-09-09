package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoliceHolderShortTerm {

    @JsonAlias({"heldInsuranceLast39Days","held-insurance-last-39-days"})
    private String heldInsuranceLast39Days;
    @JsonAlias({"periodCompCarInsurance","period-comp-car-insurance"})
    private String periodCompCarInsurance;
    @JsonAlias({"periodCompNonMotorInsurance","period-comp-nonmotor-insurance"})
    private String periodCompNonMotorInsurance;
    @JsonAlias({"hasConsent","has-consent"})
    private String hasConsent;
    @JsonAlias({"licenseDetail","license-detail"})
    private LicenseDetail[] licenseDetail;
}
