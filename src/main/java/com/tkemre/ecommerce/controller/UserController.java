package com.tkemre.ecommerce.controller;

import com.tkemre.ecommerce.dto.AddressDto;
import com.tkemre.ecommerce.dto.AddressRequest;
import com.tkemre.ecommerce.dto.UpdateUserRequest;
import com.tkemre.ecommerce.dto.UserDto;
import com.tkemre.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Yetkilendirme için
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Mevcut kullanıcıyı almak için
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users") // Kullanıcı işlemleri için base path
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Kullanıcı profili getir
    // GET /api/v1/users/profile
    @GetMapping("/profile")
    // Bu endpoint'e sadece kimliği doğrulanmış kullanıcılar erişebilir.
    // @AuthenticationPrincipal ile JWT'den gelen kullanıcı detaylarını alıyoruz.
    public ResponseEntity<UserDto> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // userDetails.getUsername() ile oturum açmış kullanıcının adını alıyoruz
        UserDto userDto = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(userDto);
    }

    // Kullanıcı profili güncelle
    // PUT /api/v1/users/profile
    @PutMapping("/profile")
    // Kimliği doğrulanmış her kullanıcı kendi profilini güncelleyebilir.
    public ResponseEntity<UserDto> updateUserProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody UpdateUserRequest request) {
        UserDto updatedUserDto = userService.updateUserProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updatedUserDto);
    }

    // Adres ekle
    // POST /api/v1/users/address
    @PostMapping("/address")
    // Kimliği doğrulanmış her kullanıcı kendine adres ekleyebilir.
    public ResponseEntity<AddressDto> addAddress(@AuthenticationPrincipal UserDetails userDetails,
                                                 @Valid @RequestBody AddressRequest request) {
        AddressDto addressDto = userService.addAddress(userDetails.getUsername(), request);
        return ResponseEntity.ok(addressDto); // Adres eklendiğinde 200 OK döndürür
    }


}