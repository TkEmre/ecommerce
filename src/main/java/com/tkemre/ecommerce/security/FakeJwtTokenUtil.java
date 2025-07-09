package com.tkemre.ecommerce.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class FakeJwtTokenUtil extends JwtTokenUtil {
    @Override
    public String generateToken(Authentication authentication) {
        return "fake-jwt-token"; // Test için sabit token
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        return true; // Her token'ı geçerli say
    }

    @Override
    public String extractUsername(String token) {
        return "emre@example.com"; // Token'dan hep aynı kullanıcıyı döndür
    }
}
