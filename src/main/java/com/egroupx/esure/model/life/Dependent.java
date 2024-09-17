package com.egroupx.esure.model.life;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dependent {

    private String client;
    private String title;
    private String firstName;
    private String surname;
    private String idNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String age;
    private String mainMemberID;
    private String policyNumber;
    private String function;
}
