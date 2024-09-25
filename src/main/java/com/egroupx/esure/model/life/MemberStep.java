package com.egroupx.esure.model.life;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberStep {

    private Long id;
    private Long pol_360_main_member_id;
    private String idNumber;
    private String step;
}
