package com.tkemre.ecommerce.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FakeAuthentication implements Authentication {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // Yetkileri test etmiyoruz
    }

    @Override
    public Object getCredentials() {
        return "mocked-credentials";
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return "emre@example.com"; // Veya test ettiğin kullanıcı
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // Gerek yok
    }

    @Override
    public String getName() {
        return "emre@example.com";
    }
}
