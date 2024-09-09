package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdContentsResponse {
    private Long id;
    private String sumInsured;
    private String description;
    private String restrictedCover;
    private String unoccPeriod;
    private String unrelatedCount;
    private String standardWalls;
    private String thatchedRoof;
    private String burglarBars;
    private String securityGates;
    private String recentLossCount;
    private String propertyOwnedClaim;
    private String alarmInWorkingOrder;
    private ShLinkResponse[] shLink;
}
