package com.egroupx.esure.model.responses;

import com.egroupx.esure.model.ShLink;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdContentsResponse {
    private Long id;
    @JsonProperty("sum.insured")
    private String sumInsured;
    private String description;
    @JsonProperty("restricted.cover")
    private String restrictedCover;
    @JsonProperty("unocc.period")
    private String unoccPeriod;
    @JsonProperty("unrelated.count")
    private String unrelatedCount;
    @JsonProperty("standard.walls")
    private String standardWalls;
    @JsonProperty("thatched.roof")
    private String thatchedRoof;
    @JsonProperty("burglar.bars")
    private String burglarBars;
    @JsonProperty("security.gates")
    private String securityGates;
    @JsonProperty("alarm.in.working.order")
    private String alarmInWorkingOrder;
    @JsonProperty("recent.loss.count")
    private String recentLossCount;
    @JsonProperty("property-owned-claim")
    private String propertyOwnedClaim;
    @JsonProperty("sh-link")
    private ShLinkResponse[] shLinks;
}
