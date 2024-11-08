package com.egroupx.esure.dto.fsp_qoute.personal_details;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolder {

    private Long id;
    @JsonAlias("isPrivate")
    @JsonProperty("private-or-org")
    private String isPrivate;
    private Person person;
    @JsonAlias("shortTerm")
    @JsonProperty("short-term")
    private PoliceHolderShortTerm shortTerm;
    @JsonAlias("incomeDetails")
    @JsonProperty("income-details")
    private IncomeDetails incomeDetails;
    @JsonAlias("addresses")
    @JsonProperty("address")
    private List<GenericAddress> addresses;

}
