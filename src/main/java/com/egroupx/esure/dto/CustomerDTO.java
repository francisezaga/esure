package com.egroupx.esure.dto;


import com.egroupx.esure.enums.Gender;
import com.egroupx.esure.enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private String idNumber;
    private String firstName;
    private String lastName;
    private Date dob;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private String address;
    private String email;
    private String cellNumber;
    private boolean wouldLikeToHearMore;

}
