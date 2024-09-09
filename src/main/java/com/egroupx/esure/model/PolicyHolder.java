package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolder {

    private Long id;
    @JsonAlias({"isPrivate","private-or-org"})
    private String isPrivate;
    private Person person;
    private PoliceHolderShortTerm shortTerm;
    private IncomeDetails incomeDetails;
    private Object[] address;

}
