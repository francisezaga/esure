package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolder {
    private Long id;
    private Long fspQuoteRefId;
    private Long fspPhRefId;
    private String trust;
    private String isPrivate;
    private String stakeholderType;
    private Customer person;
    private PHShortTerm shortTerm;
    private PHIncomeDetails incomeDetails;
    //private InsuranceDetails insuranceDetails;
    private List<Object> addresses;
}
