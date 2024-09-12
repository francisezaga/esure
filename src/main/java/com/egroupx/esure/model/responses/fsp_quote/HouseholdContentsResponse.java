package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdContentsResponse {
    private Long id;
    @JsonAlias("sum.insured")
    private String sumInsured;
    private String description;
    @JsonAlias("restricted.cover")
    private String restrictedCover;
    @JsonAlias("unocc.period")
    private String unoccPeriod;
    @JsonAlias("unrelated.count")
    private String unrelatedCount;
    @JsonAlias("standard.walls")
    private String standardWalls;
    @JsonAlias("thatched.roof")
    private String thatchedRoof;
    @JsonAlias("burglar.bars")
    private String burglarBars;
    @JsonAlias("security.gates")
    private String securityGates;
    @JsonAlias("alarm.in.working.order")
    private String alarmInWorkingOrder;
    @JsonAlias("recent.loss.count")
    private String recentLossCount;
    @JsonAlias("property-owned-claim")
    private String propertyOwnedClaim;
    @JsonAlias("sh-link")
    private ShLinkResponse[] shLinks;
}
