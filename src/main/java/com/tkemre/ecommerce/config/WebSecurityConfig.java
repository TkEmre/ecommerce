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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
                                        "/api/v1/auth/**",           // Kayıt ve Giriş endpoint'leri
                                        "/swagger-ui/**",            // Swagger UI statik kaynakları
                                        "/v3/api-docs/**",           // OpenAPI dokümantasyon API'si
                                        "/swagger-resources/**",
                                        "/configuration/**",
                                        "/webjars/**",
                                        "/swagger-ui.html",
                                        "/h2-console/**"             // H2 Konsolu (sadece geliştirme için)
                                ).permitAll() // Belirtilen yollara herkesin erişimine izin ver
                                .anyRequest().authenticated(); // Diğer tüm istekler kimlik doğrulaması gerektirir
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
