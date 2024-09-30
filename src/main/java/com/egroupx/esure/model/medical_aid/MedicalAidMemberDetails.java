package com.egroupx.esure.model.medical_aid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalAidMemberDetails {

    private Long id;
    private int adultsCount;
    private int childrenCount;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean hasMedicalAid;
    private String incomeCategory;
    private String hospitalChoice;
    private String hospitalRates;
    private String dayToDayCoverLevel;
    private String doctorChoice;
    private boolean hasChronicMedicationRequirements;
    private String hospitalExclusions;
}
