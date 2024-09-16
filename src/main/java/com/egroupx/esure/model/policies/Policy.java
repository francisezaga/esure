package com.egroupx.esure.model.policies;

import com.egroupx.esure.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

        private Long id;
        private Long insurerId;
        private Long fspPolicyId;
        private int categoryId;
        private LocalDate dateQuoted;
        private String status;
        private String idNumber;
        private Long brokerCode;
        private String externalPolicyNo;
        private Long quotationId;
        private Double quotedPremium;
        private Long offeringId;
        private String offeringName;
        private String errorStatus;
        private String eSureStatus;
        private PolicyAdditionalInfo additionalInfo;
        private List<PolicyHolder> policyHolders;
        private List<AllRisks> allRisks;
        private List<Building> buildings;
        private List<HouseHoldContents> householdContents;
        private List<MotorVehicles> motorVehicles;

}
