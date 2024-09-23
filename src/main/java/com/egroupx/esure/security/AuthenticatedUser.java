package com.egroupx.esure.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AuthenticatedUser implements UserDetails {
    private String username;
    private Collection authorities;

    public AuthenticatedUser(String username){
        this.username = username;
    }

    public AuthenticatedUser(String username, Collection authorities){
        this.username = username;
        this.authorities = authorities;
    }

    @Override
    public Collection getAuthorities() {
        return this.authorities;
    }
    @Override
    public String getPassword() {
        return null;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
