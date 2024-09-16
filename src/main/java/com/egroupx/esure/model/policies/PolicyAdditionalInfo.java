package com.egroupx.esure.model.policies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyAdditionalInfo {

   private Long fspQuoteRefId;
   private Long fspPolicyId;
   private String contact;
   private String instructions;
}
