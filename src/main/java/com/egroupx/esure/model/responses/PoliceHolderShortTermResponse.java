package com.egroupx.esure.model.responses;

import com.egroupx.esure.model.LicenseDetail;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoliceHolderShortTermResponse {

    @JsonProperty("held-insurance-last-39-days")
    private String heldInsuranceLast39Days;
    @JsonProperty("period-comp-car-insurance")
    private String periodCompCarInsurance;
    @JsonProperty("period-comp-nonmotor-insurance")
    private String periodCompNonMotorInsurance;
    @JsonProperty("has-consent")
    private String hasConsent;
    @JsonProperty("license-detail")
    private LicenseDetailResponse[] licenseDetails;
}
