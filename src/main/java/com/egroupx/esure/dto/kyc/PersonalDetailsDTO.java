package com.egroupx.esure.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PersonalDetailsDTO {

    private String firstName;
    private String lastName;
    private String typeOfId;
    private String idNumber;
    private String entityType;

}
