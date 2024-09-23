package com.egroupx.esure.model.life;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedMember {

    @JsonAlias("client")
    @JsonProperty("Client")
    private String client;
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
    @JsonAlias("mainMemberId")
    @JsonProperty("MainMemberID")
    private String mainMemberID;
    @JsonAlias("policyNumber")
    @JsonProperty("PolicyNumber")
    private String policyNumber;
    @JsonAlias("function")
    @JsonProperty("Function")
    private String function;
    @JsonAlias("relation")
    @JsonProperty("Relation")
    private String relation;
}
