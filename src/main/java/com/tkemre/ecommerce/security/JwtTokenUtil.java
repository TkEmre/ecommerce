package com.tkemre.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // Token geçerlilik süresi (milisaniye)

    // Token'dan kullanıcı adını (subject) çıkarır
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Token'dan son kullanma tarihini çıkarır
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Belirli bir claim'i token'dan çıkarır
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Token'daki tüm claim'leri (iddiaları) çıkarır
    private Claims extractAllClaims(String token) {
        // JJWT 0.11.0 ve üzeri sürümler için önerilen kullanım Jwts.parserBuilder()
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build() // Builder'ı build() ile bitirip JwtParser objesini alıyoruz
                .parseClaimsJws(token)
                .getBody();
    }

    // Token'ın süresinin dolup dolmadığını kontrol eder
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Kullanıcıya özel token oluşturur (Authentication objesi alarak rolleri de ekler)
    public String generateToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);

        return createToken(claims, authentication.getName());
    }

    // Token oluşturma (private helper metot)
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Token'ı doğrular
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Secret key'i byte dizisinden Key objesine dönüştürür
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
