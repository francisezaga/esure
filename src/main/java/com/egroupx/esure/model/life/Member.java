package com.egroupx.esure.model.life;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private String client;
    private String agentCode;
    private String policyNumber;
    private String brokerCode;
    private String title;
    private String firstName;
    private String surname;
    private String idNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String age;
    private String cellNumber;
    private String altCellNumber;
    private String workNumber;
    private String homeNumber;
    private String email;
    private String fax;
    private String contactType;
    private String postalAddress1;
    private String postalAddress2;
    private String postalAddress3;
    private String postalCode;
    private String residentialAddress1;
    private String residentialAddress2;
    private String residentialAddress3;
    private String residentialCode;
    private String function;
    private String memberType;
    private String premium;
    private String cover;
    private String addPolicyID;
    private String statusCode;
}

