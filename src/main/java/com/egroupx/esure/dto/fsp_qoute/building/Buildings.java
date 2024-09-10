package com.egroupx.esure.dto.fsp_qoute.building;

import com.egroupx.esure.dto.fsp_qoute.ShLink;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Buildings {

    @JsonAlias("sumInsured")
    @JsonProperty("sum.insured")
    private String sumInsured;
    private String description;
    @JsonAlias("coverType")
    @JsonProperty("cover.type")
    private String coverType;
    @JsonAlias("roofConstruction")
    @JsonProperty("roof.construction")
    private String roofConstruction;
    private String construction;
    @JsonAlias("geyserCover")
    @JsonProperty("geysercover")
    private String geyserCover;
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
