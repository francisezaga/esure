package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoliceHolderShortTermResponse {

    @JsonAlias("held-insurance-last-39-days")
    private String heldInsuranceLast39Days;
    @JsonAlias("period-comp-car-insurance")
    private String periodCompCarInsurance;
    @JsonAlias("period-comp-nonmotor-insurance")
    private String periodCompNonMotorInsurance;
    @JsonAlias("has-consent")
    private String hasConsent;
    @JsonAlias("license-detail")
    private LicenseDetailResponse[] licenseDetails;
}
