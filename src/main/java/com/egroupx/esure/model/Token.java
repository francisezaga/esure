package com.egroupx.esure.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private UUID uuid;
    private String tokenName;
    private String token;
    private String username;
    private String password;
}
