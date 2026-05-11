package com.tkemre.ecommerce.config;

import com.tkemre.ecommerce.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile; // Bu import kaldırıldı
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
// Eğer bu bean'ler ApplicationConfig'e taşındıysa, aşağıdaki import'lar da kaldırılabilir.
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final Environment environment;

    public WebSecurityConfig(JwtRequestFilter jwtRequestFilter, Environment environment) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.environment = environment;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // CSRF korumasını devre dışı bırak
                .authorizeHttpRequests(authorize -> {
                    // Test profili aktif mi diye Environment üzerinden kontrol et
                    if (environment.acceptsProfiles(Profiles.of("test"))) {
                        System.out.println("DEBUG: WebSecurityConfig - Test profili aktif. Tüm isteklere izin veriliyor."); // Debug logu
                        authorize.anyRequest().permitAll(); // Test profili için her şeye izin ver
                    } else {
                        System.out.println("DEBUG: WebSecurityConfig - Test dışı profili aktif. Belirli güvenlik kuralları uygulanıyor."); // Debug logu
                        // Diğer (dev, prod vb.) profiller için güvenlik kuralları
                        authorize
                                .requestMatchers(
                                        "/api/v1/auth/**",
                                        "/api/v1/products",
                                        "/api/v1/products/**",
                                        "/uploads/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/configuration/**",
                                        "/webjars/**",
                                        "/swagger-ui.html",
                                        "/h2-console/**"
                                ).permitAll()
                                .anyRequest().authenticated();
                    }
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // H2 konsolu gibi iframe kullanan sayfalar için frameOptions'ı devre dışı bırak
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}
