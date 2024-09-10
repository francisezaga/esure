package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdContents {
    @JsonAlias("sumInsured")
    @JsonProperty("sum.insured")
    private String sumInsured;
    private String description;
    @JsonAlias("restrictedCover")
    @JsonProperty("restricted.cover")
    private String restrictedCover;
    @JsonAlias("unoccPeriod")
    @JsonProperty("unocc.period")
    private String unoccPeriod;
    @JsonAlias("unrelatedCount")
    @JsonProperty("unrelated.count")
    private String unrelatedCount;
    @JsonAlias("standardWalls")
    @JsonProperty("standard.walls")
    private String standardWalls;
    @JsonAlias("thatchedRoof")
    @JsonProperty("thatched.roof")
    private String thatchedRoof;
    @JsonAlias("burglarBars")
    @JsonProperty("burglar.bars")
    private String burglarBars;
    @JsonAlias("securityGates")
    @JsonProperty("security.gates")
    private String securityGates;
    @JsonAlias("recentLossCount")
    @JsonProperty("recent.loss.count")
    private String recentLossCount;
    @JsonAlias("propertyOwnedClaim")
    @JsonProperty("property-owned-claim")
    private String propertyOwnedClaim;
    @JsonAlias("shLinks")
    @JsonProperty("sh-link")
    private ShLink[] shLinks;
}
