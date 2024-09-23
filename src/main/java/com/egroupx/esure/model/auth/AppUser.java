package com.egroupx.esure.model.auth;

import com.egroupx.esure.security.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Getter
    private String username;
    private String password;

    public AuthenticatedUser toAuthUser() {
        // returns a AuthenticatedUser  object
        return new AuthenticatedUser(username,null);

    }

    public String getPassword() {
        return password;
    }
}
