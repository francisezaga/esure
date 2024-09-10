package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoliceHolderShortTerm {

    @JsonAlias("heldInsuranceLast39Days")
    @JsonProperty("held-insurance-last-39-days")
    private String heldInsuranceLast39Days;
    @JsonAlias("periodCompCarInsurance")
    @JsonProperty("period-comp-car-insurance")
    private String periodCompCarInsurance;
    @JsonAlias("periodCompNonMotorInsurance")
    @JsonProperty("period-comp-nonmotor-insurance")
    private String periodCompNonMotorInsurance;
    @JsonAlias("hasConsent")
    @JsonProperty("has-consent")
    private String hasConsent;
    @JsonAlias("licenseDetails")
    @JsonProperty("license-detail")
    private LicenseDetail[] licenseDetails;
}
