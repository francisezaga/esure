package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Buildings {

    @JsonAlias({"sumInsured","sum.insured"})
    private String sumInsured;
    private String description;
    @JsonAlias({"coverType","cover.type"})
    private String coverType;
    @JsonAlias({"roofConstruction","roof.construction"})
    private String roofConstruction;
    private String construction;
    @JsonAlias({"geyserCover","geysercover"})
    private String geyserCover;
    @JsonAlias({"recentLossCount","recent.loss.count"})
    private String recentLossCount;
    @JsonAlias({"propertyOwnedClaim","property-owned-claim"})
    private String propertyOwnedClaim;
    @JsonAlias({"shLink","sh-link"})
    private ShLink[] shLink;
}
