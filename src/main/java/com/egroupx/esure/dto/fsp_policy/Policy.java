package com.egroupx.esure.dto.fsp_policy;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    @JsonAlias("policyHolders")
    @JsonProperty("PolicyHolder")
    private PolicyHolder[] policyHolders;
    private String[] instructions;
}
