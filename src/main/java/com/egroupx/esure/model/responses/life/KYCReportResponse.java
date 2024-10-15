package com.egroupx.esure.model.responses.life;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYCReportResponse {

    private String pol_360_mainMemberid;
    private String policyNumber;
    private String kycReport;
}
