package com.egroupx.esure.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="token")
public class Token {
    private UUID uuid;
    private String tokenName;
    private String token;
    private String username;
    private String password;
}
