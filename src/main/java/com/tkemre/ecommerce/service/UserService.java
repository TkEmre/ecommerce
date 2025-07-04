package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.dto.LoginRequest;
import com.tkemre.ecommerce.dto.RegisterRequest;
import com.tkemre.ecommerce.dto.UserDto;
import com.tkemre.ecommerce.dto.UpdateUserRequest; // Profil güncelleme için yeni DTO
import com.tkemre.ecommerce.dto.AddressRequest; // Adres ekleme için yeni DTO
import com.tkemre.ecommerce.dto.AddressDto; // Adres dönüş DTO'su

public interface UserService {
    UserDto register(RegisterRequest request);
    String login(LoginRequest request); // JWT token döndürecek
    UserDto getUserProfile(String username); // Kullanıcı profili için
    UserDto updateUserProfile(String username, UpdateUserRequest request); // Profil güncelleme
    AddressDto addAddress(String username, AddressRequest request); // Adres ekleme
}