package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolderResponse {

    private Long id;
    private String isPrivate;
    private String trust;
    private String stakeHolderType;
    private PersonResponse person;
    private PoliceHolderShortTermResponse shortTerm;
    private IncomeDetailsResponse incomeDetails;
    private InsuranceDetailsResponse insuranceDetails;
    private Object[] address;

}
