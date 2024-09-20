package com.egroupx.esure.model.responses.life;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LifeAPIResponse {

    @JsonAlias("Result")
    private String result;
    @JsonAlias("Message")
    private String message;
    @JsonAlias("PolicyNumber")
    private String policyNumber;
    @JsonAlias("MemberID")
    private Long memberID;
}