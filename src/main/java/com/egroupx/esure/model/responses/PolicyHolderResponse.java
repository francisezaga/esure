package com.egroupx.esure.model.responses;

import com.egroupx.esure.model.IncomeDetails;
import com.egroupx.esure.model.Person;
import com.egroupx.esure.model.PoliceHolderShortTerm;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolderResponse {

    private Long id;
    @JsonProperty("private-or-org")
    private String isPrivate;
    private String trust;
    @JsonProperty("stakeholder-type")
    private String stakeHolderType;
    private PersonResponse person;
    @JsonProperty("short-term")
    private PoliceHolderShortTermResponse shortTerm;
    @JsonProperty("income-details")
    private IncomeDetailsResponse incomeDetails;
    @JsonProperty("insurance-details")
    private InsuranceDetailsResponse insuranceDetails;
    @JsonProperty("address")
    private Object[] address;

}
