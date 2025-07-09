package com.tkemre.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment; // Environment import'u
import org.springframework.core.env.Profiles; // Profiles import'u
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final Environment environment; // Environment enjekte edildi
    private final TokenBlacklistService tokenBlacklistService; // TokenBlacklistService enjekte edildi


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Eğer 'test' profili aktifse, bu filtrenin içindeki güvenlik kontrolünü tamamen atla
        // ve isteği doğrudan bir sonraki filtreye/Controller'a gönder.
        if (environment.acceptsProfiles(Profiles.of("test"))) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            // JWT token'ın kara listede olup olmadığını kontrol et
            if (tokenBlacklistService.isBlacklisted(jwt)) {
                // Eğer token kara listedeyse, yetkisiz yanıt döndür ve isteği sonlandır
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401 Unauthorized
                response.getWriter().write("Token has been blacklisted."); // Yanıt mesajı
                return; // Filtre zincirini burada kes
            }

            try {
                username = jwtTokenUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Token doğrulama (süre dolumu, imza hatası vb.) başarısız olursa
                System.out.println("JWT Token validation failed: " + e.getMessage());
                // username null kalacak, bu da sonraki kimlik doğrulama bloğuna girilmesini engeller
            }
        }

        // Kullanıcı adı çıkarıldıysa ve güvenlik bağlamında henüz bir kimlik doğrulama yoksa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Token'ın geçerliliğini (süresi dolmamış ve kullanıcıya ait) doğrula
            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response); // İsteği filtre zincirinde bir sonraki adıma ilet
    }
}
