package com.egroupx.esure.model.responses.fsp_qoute_policies;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequirementResponse {

    private String seq;
    private String type;
    @JsonAlias("type-desc")
    private String typeDesc;
    private String amount;
    private String val;
}
