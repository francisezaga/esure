package com.egroupx.esure.dto.life;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryDTO {

    @JsonAlias("policyNumber")
    @JsonProperty("PolicyNumber")
    private String policyNumber;
    @JsonAlias("client")
    @JsonProperty("Client")
    private String client;
    @JsonAlias("contactCell")
    @JsonProperty("ContactCell")
    private String contactCell;
    @JsonAlias("contactWorkTell")
    @JsonProperty("ContactWorkTell")
    private String contactWorkTell;
    @JsonAlias("contactHomeTell")
    @JsonProperty("ContactHomeTell")
    private String contactHomeTell;
    @JsonAlias("contactFax")
    @JsonProperty("ContactFax")
    private String contactFax;
    @JsonAlias("contactEmail")
    @JsonProperty("ContactEmail")
    private String contactEmail;
    @JsonAlias("sessionUserID")
    @JsonProperty("SessionUserID")
    private String sessionUserID;
    @JsonAlias("prefType")
    @JsonProperty("PrefType")
    private String prefType;
    @JsonAlias("benLastName")
    @JsonProperty("BenLastName")
    private String benLastName;
    @JsonAlias("benFirstName")
    @JsonProperty("BenFirstName")
    private String benFirstName;
    @JsonAlias("benIDNumber")
    @JsonProperty("BenIDNumber")
    private String benIDNumber;
    @JsonAlias("benPercentage")
    @JsonProperty("BenPercentage")
    private String benPercentage;
    @JsonAlias("benDOB")
    @JsonProperty("BenDOB")
    private String benDOB;
    @JsonAlias("benRelation")
    @JsonProperty("BenRelation")
    private String benRelation;
    @JsonAlias("benType")
    @JsonProperty("BeneficiaryType")
    private String benType;
    @JsonAlias("title")
    @JsonProperty("Title")
    private String title;
    @JsonAlias("mainMemberId")
    @JsonProperty("MainMemberID")
    private String mainMemberID;
    @JsonAlias("function")
    @JsonProperty("Function")
    private String function;

}
