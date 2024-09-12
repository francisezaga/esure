package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PHShortTerm {
    private Long id;
    private Long fspQuoteRefId;
    private Long fspPhRefId;
    private String heldInsuranceLast_39_days;
    private String periodCompCarInsurance;
    private String periodCompNonMotorInsurance;
    private String hasConsent;
    private List<PHLicenseDetail> phLicenseDetailList;
}

