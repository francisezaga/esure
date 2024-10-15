package com.egroupx.esure.model.life;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYCReportModel {

    private String memberId;
    private String policyNumber;
    private String kycReport;
}
