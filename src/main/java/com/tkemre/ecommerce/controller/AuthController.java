package com.tkemre.ecommerce.controller;

import com.tkemre.ecommerce.dto.LoginRequest;
import com.tkemre.ecommerce.dto.RegisterRequest;
import com.tkemre.ecommerce.dto.UserDto;
import com.tkemre.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {

        UserDto registeredUser = userService.register(request);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        // Kullanıcı giriş işlemini çağır ve JWT token'ı al
        String jwtToken = userService.login(request);
        // JWT token'ı HTTP yanıtının gövdesinde döndür
        return ResponseEntity.ok(jwtToken);
    }
}