package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingsResponse {

    private Long id;
    @JsonAlias("sum.insured")
    private String sumInsured;
    private String description;
    @JsonAlias("cover.type")
    private String coverType;
    @JsonAlias("roof.construction")
    private String roofConstruction;
    private String construction;
    @JsonAlias("geysercover")
    private String geyserCover;
    @JsonAlias("recent.loss.count")
    private String recentLossCount;
    @JsonAlias("property-owned-claim")
    private String propertyOwnedClaim;
    @JsonAlias("sh-link")
    private ShLinkResponse[] shLinks;
}
