package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Buildings {

    private String sumInsured;
    private String description;
    private String coverType;
    private String roofConstruction;
    private String construction;
    private String geyserCover;
    private String recentLossCount;
    private String propertyOwnedClaim;
    private ShLink[] shLink;
}
