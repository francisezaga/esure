package com.egroupx.esure.model.responses.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitizenResponseBody {
    @JsonProperty("TrackingNumber")
    private String TrackingNumber;

    @JsonProperty("Status")
    private String Status;
}
