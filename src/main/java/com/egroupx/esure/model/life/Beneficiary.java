package com.egroupx.esure.model.life;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {

    private String policyNumber;
    private String client;
    private String contactCell;
    private String contactWorkTell;
    private String contactHomeTell;
    private String contactFax;
    private String contactEmail;
    private String sessionUserID;
    private String prefType;
    private String benLastName;
    private String benFirstName;
    private String benIDNumber;
    private String benPercentage;
    private LocalDate benDOB;
    private String benRelation;
    private String beneficiaryType;
    private String title;
    private String function;
}