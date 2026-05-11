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
import com.tkemre.ecommerce.dto.AddressDto;
import com.tkemre.ecommerce.dto.LoginRequest;
import com.tkemre.ecommerce.dto.RegisterRequest;
import com.tkemre.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
    // GET /api/v1/users/all?page=0&size=10&sort=username,asc
    @GetMapping("/all") // Endpoint path'i
    @PreAuthorize("hasRole('ADMIN')") // Sadece ADMIN rolüne sahip kullanıcılar erişebilir
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) { // Sayfalama ve sıralama parametreleri

        // Sıralama parametrelerini işle
        Sort sorting = Sort.by(sort[0]);
        if (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) {
            sorting = sorting.descending();
        } else {
            sorting = sorting.ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }


}