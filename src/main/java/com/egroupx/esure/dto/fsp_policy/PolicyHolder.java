package com.egroupx.esure.dto.fsp_policy;

import com.egroupx.esure.dto.fsp_qoute.personal_details.TelAddress;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolder {

    @JsonAlias("addresses")
    @JsonProperty("address")
    private TelAddress[] addresses;

}
