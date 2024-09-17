package com.egroupx.esure.model.responses.life;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @JsonAlias("PolicyCode")
    private String policyCode;
    @JsonAlias("PolicyDescription")
    private String policyDescription;
    @JsonAlias("MaxNumberDependants")
    private String maxNumberDependants;
    @JsonAlias("MaxNumberSpouse")
    private String maxNumberSpouse;
    @JsonAlias("AllowFreeExtended")
    private String allowFreeExtended;
    @JsonAlias("MaxNumberFreeExtended")
    private String maxNumberFreeExtended;
    @JsonAlias("ProdType")
    private String prodType;
    @JsonAlias("ProductTerms")
    private String productTerms;
    @JsonAlias("DetDescription")
    private String detDescription;
    @JsonAlias("AdditionalPolicyInfo")
    private List<AdditionalPolicyInfo> additionalPolicyInfoList;
}
