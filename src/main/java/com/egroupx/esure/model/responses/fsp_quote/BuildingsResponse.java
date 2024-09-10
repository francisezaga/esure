package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingsResponse {

    private Long id;
    @JsonProperty("sum.insured")
    private String sumInsured;
    private String description;
    @JsonProperty("cover.type")
    private String coverType;
    @JsonProperty("roof.construction")
    private String roofConstruction;
    private String construction;
    @JsonProperty("geysercover")
    private String geyserCover;
    @JsonProperty("recent.loss.count")
    private String recentLossCount;
    @JsonProperty("property-owned-claim")
    private String propertyOwnedClaim;
    @JsonProperty("sh-link")
    private ShLinkResponse[] shLinks;
}
