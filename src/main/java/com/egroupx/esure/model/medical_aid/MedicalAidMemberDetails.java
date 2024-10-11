package com.egroupx.esure.model.medical_aid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalAidMemberDetails {

    private Long id;
    private int adultsCount;
    private int childrenCount;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;
    private boolean hasMedicalAid;
    private String nameOfMedicalAidProvider;
    private boolean isGrossIncomeMoreThan14K;
    private String budgetedAmount;
    private String medicalPriority;
    private boolean isNetIncomeMoreThan14k;
    private boolean memberOrDependentHasChronicMedRequirements;

}
