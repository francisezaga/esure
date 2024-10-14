package com.egroupx.esure.dto.life;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    @JsonAlias("client")
    @JsonProperty("Client")
    private String client;
    @JsonAlias("agentCode")
    @JsonProperty("AgentCode")
    private String agentCode;
    @JsonProperty("referralCode")
    private String referralCode;
    @JsonAlias("policyNumber")
    @JsonProperty("PolicyNumber")
    private String policyNumber;
    @JsonAlias("brokerCode")
    @JsonProperty("BrokerCode")
    private String brokerCode;
    @JsonAlias("title")
    @JsonProperty("Title")
    private String title;
    @JsonAlias("firstName")
    @JsonProperty("FirstName")
    private String firstName;
    @JsonAlias("surname")
    @JsonProperty("Surname")
    private String surname;
    @JsonAlias("idNumber")
    @JsonProperty("IDNumber")
    private String idNumber;
    @JsonAlias("gender")
    @JsonProperty("Gender")
    private String gender;
    @JsonAlias("dateOfBirth")
    @JsonProperty("DateOfBirth")
    private String dateOfBirth;
    @JsonAlias("age")
    @JsonProperty("Age")
    private String age;
    @JsonAlias("cellNumber")
    @JsonProperty("CellNumber")
    private String cellNumber;
    @JsonAlias("altCellNumber")
    @JsonProperty("AltCellNumber")
    private String altCellNumber;
    @JsonAlias("workNumber")
    @JsonProperty("WorkNumber")
    private String workNumber;
    @JsonAlias("homeNumber")
    @JsonProperty("HomeNumber")
    private String homeNumber;
    @JsonAlias("email")
    @JsonProperty("Email")
    private String email;
    @JsonAlias("fax")
    @JsonProperty("Fax")
    private String fax;
    @JsonAlias("contactType")
    @JsonProperty("ContactType")
    private String contactType;
    @JsonAlias("postalAddress1")
    @JsonProperty("PostalAddress1")
    private String postalAddress1;
    @JsonAlias("postalAddress2")
    @JsonProperty("PostalAddress2")
    private String postalAddress2;
    @JsonAlias("postalAddress3")
    @JsonProperty("PostalAddress3")
    private String postalAddress3;
    @JsonAlias("postalCode")
    @JsonProperty("PostalCode")
    private String postalCode;
    @JsonAlias("residentialAddress1")
    @JsonProperty("ResidentialAddress1")
    private String residentialAddress1;
    @JsonAlias("residentialAddress2")
    @JsonProperty("ResidentialAddress2")
    private String residentialAddress2;
    @JsonAlias("residentialAddress3")
    @JsonProperty("ResidentialAddress3")
    private String residentialAddress3;
    @JsonAlias("residentialCode")
    @JsonProperty("ResidentialCode")
    private String residentialCode;
    @JsonAlias("function")
    @JsonProperty("Function")
    private String function;
    @JsonAlias("memberType")
    @JsonProperty("MemberType")
    private String memberType;
    @JsonAlias("premium")
    @JsonProperty("Premium")
    private String premium;
    @JsonAlias("cover")
    @JsonProperty("Cover")
    private String cover;
    @JsonAlias("addPolicyID")
    @JsonProperty("AddPolicyID")
    private String addPolicyID;
    @JsonAlias("statusCode")
    @JsonProperty("StatusCode")
    private String statusCode;
}

