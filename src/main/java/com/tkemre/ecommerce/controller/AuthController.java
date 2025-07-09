package com.tkemre.ecommerce.controller;

import com.tkemre.ecommerce.dto.LoginRequest;
import com.tkemre.ecommerce.dto.RegisterRequest;
import com.tkemre.ecommerce.dto.UserDto; // UserDto kullanmaya devam ettiğiniz varsayımıyla
import com.tkemre.ecommerce.service.UserService;
import com.tkemre.ecommerce.security.JwtTokenUtil; // JwtTokenUtil import'u
import com.tkemre.ecommerce.security.TokenBlacklistService; // TokenBlacklistService import'u
import jakarta.servlet.http.HttpServletRequest; // HttpServletRequest import'u
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // PreAuthorize için
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Swagger UI için

@RestController
@RequestMapping("/api/v1/auth")
// Bu controller'daki çoğu endpoint için JWT gereksinimi (login/register hariç)
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil; // Yeni eklendi
    private final TokenBlacklistService tokenBlacklistService; // Yeni eklendi

    // Lombok @RequiredArgsConstructor kullanıyorsanız bu constructor'a gerek kalmaz.
    // Ancak manuel olarak bağımlılıkları enjekte etmek için bu constructor'ı kullanıyoruz.
    public AuthController(UserService userService, JwtTokenUtil jwtTokenUtil, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    @SecurityRequirement(name = "none") // Kayıt endpoint'i için güvenlik gerektirme
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        // Not: register metodu UserProfileDto döndürüyor olabilir, UserDto yerine kontrol edin.
        // Eğer UserProfileDto kullanıyorsanız, burayı UserProfileDto olarak güncelleyin.
        UserDto registeredUser = userService.register(request); // Metot adı registerUser olarak düzeltildi varsayımıyla
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @SecurityRequirement(name = "none") // Giriş endpoint'i için güvenlik gerektirme
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        // Kullanıcı giriş işlemini çağır ve JWT token'ı al
        String jwtToken = userService.login(request); // Metot adı loginUser olarak düzeltildi varsayımıyla
        // JWT token'ı HTTP yanıtının gövdesinde döndür
        return ResponseEntity.ok(jwtToken);
    }

    // Logout endpoint'i
    // POST /api/v1/auth/logout
    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Giriş yapmış herhangi bir kullanıcı çıkış yapabilir
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7); // "Bearer " kısmını atla
            try {
                // Token'ın son kullanma tarihini çek ve kara listeye ekle
                tokenBlacklistService.blacklistToken(jwt, jwtTokenUtil.extractExpiration(jwt));
                return ResponseEntity.ok("Successfully logged out.");
            } catch (Exception e) {
                // Token'dan bilgi çıkarırken bir hata olursa (örn: geçersiz token)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or malformed token for logout: " + e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Authorization header is missing or malformed.");
    }
}
