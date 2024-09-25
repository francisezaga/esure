package com.egroupx.esure.dto.kyc;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RSAIDcheckDTO {

    private String cref;//cref0011120918081",
    private boolean ConsentReceived = true;
    private String Subsidiary = "eZaga";
    private String FirstNames;
    private String LastName;
    private String IdNumber;
    private boolean IdentityCache=true;
    private boolean CachePreferred= true;
    private boolean SAFPSRequired = true;
    private boolean LivenessRequired= false;
    private boolean HANISImageRequired =true;
    private String RequestReason = "KYC Check";
}
