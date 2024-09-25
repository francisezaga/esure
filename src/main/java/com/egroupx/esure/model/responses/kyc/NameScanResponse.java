package com.egroupx.esure.model.responses.kyc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NameScanResponse {

  private String scanId;
  private int numberOfMatches;
  private int numberOfPepMatches;
  private int numberOfSipMatches;

}