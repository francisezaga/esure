package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Building {
    private Long id;
    private Long fspQuoteRefId;
    private String description;
    private String coverType;
    private String construction;
    private String roofConstruction;
    private String sumInsured;
    private String recentLossCount;
    private String propertyOwnedClaim;
    private List<SHLink> shLinks;
}
