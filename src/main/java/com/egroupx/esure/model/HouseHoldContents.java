package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseHoldContents {
    private Long id;
    private Long fspQuoteRefId;
    private String description;
    private String sumInsured;
    private String restrictedCover;
    private String unoccPeriod;
    private String unrelatedCount;
    private String standardWalls;
    private String thatchedRoof;
    private String burglarBars;
    private String securityGates;
    private String alarmInWorkingOrder;
    private String recentLossCount;
    private String propertyOwnedClaim;
    private List<SHLink> shLinks;
}
