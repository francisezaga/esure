package com.egroupx.esure.dto.life;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendPolicySMSDTO {

    @JsonAlias("client")
    @JsonProperty("Client")
    private String client;
    @JsonAlias("function")
    @JsonProperty("Function")
    private String function;
    @JsonAlias("RelateId")
    @JsonProperty("RelateID")
    private String relatedId;
    @JsonAlias("contactNumber")
    @JsonProperty("ContactNumber")
    private String contactNumber;
    @JsonAlias("sms")
    @JsonProperty("Sms")
    private String sms;

}
