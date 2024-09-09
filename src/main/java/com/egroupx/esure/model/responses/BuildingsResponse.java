package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingsResponse {

    private Long id;
    private String sumInsured;
    private String description;
    private String coverType;
    private String roofConstruction;
    private String construction;
    private String geyserCover;
    private String recentLossCount;
    private String propertyOwnedClaim;
    private ShLinkResponse[] shLink;
}
