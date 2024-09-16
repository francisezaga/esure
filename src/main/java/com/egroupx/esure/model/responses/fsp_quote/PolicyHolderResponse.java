package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolderResponse {

    private Long id;
    @JsonAlias("private-or-org")
    private String isPrivate;
    private String trust;
    @JsonAlias("stakeholder-type")
    private String stakeHolderType;
    private PersonResponse person;
    @JsonAlias("short-term")
    private PoliceHolderShortTermResponse shortTerm;
    @JsonAlias("income-details")
    private IncomeDetailsResponse incomeDetails;
    @JsonAlias("insurance-details")
    private InsuranceDetailsResponse insuranceDetails;
    @JsonAlias("address")
    private List<AddressResponse> address;

}
