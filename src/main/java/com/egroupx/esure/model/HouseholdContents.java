package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdContents {
    @JsonAlias({"sumInsured","sum.insured"})
    private String sumInsured;
    private String description;
    @JsonAlias({"restrictedCover","restricted.cover"})
    private String restrictedCover;
    @JsonAlias({"unoccPeriod","unocc.period"})
    private String unoccPeriod;
    @JsonAlias({"unrelatedCount","unrelated.count"})
    private String unrelatedCount;
    @JsonAlias({"standardWalls","standard.walls"})
    private String standardWalls;
    @JsonAlias({"thatchedRoof","thatched.roof"})
    private String thatchedRoof;
    @JsonAlias({"burglarBars","burglar.bars"})
    private String burglarBars;
    @JsonAlias({"securityGates","security.gates"})
    private String securityGates;
    @JsonAlias({"recentLossCount","recent.loss.count"})
    private String recentLossCount;
    @JsonAlias({"propertyOwnedClaim","property-owned-claim"})
    private String propertyOwnedClaim;
    @JsonAlias({"shLink","sh-link"})
    private ShLink[] shLink;
}
