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
public class Spouse {

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
    private LocalDate dateOfBirth;
    @JsonAlias("age")
    @JsonProperty("Age")
    private String age;
    @JsonAlias("mainMember")
    @JsonProperty("MainMember")
    private String mainMemberID;
    @JsonAlias("policyNumber")
    @JsonProperty("PolicyNumber")
    private String policyNumber;
    @JsonAlias("function")
    @JsonProperty("Function")
    private String function;
}
