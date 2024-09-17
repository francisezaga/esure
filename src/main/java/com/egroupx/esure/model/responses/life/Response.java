package com.egroupx.esure.model.responses.life;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private String result;
    private String message;
    private String policyNumber;
    private Long memberID;
}
