package com.tkemre.ecommerce.dto;

import com.tkemre.ecommerce.model.UserRole;
import lombok.Builder;
import java.util.List;
import java.util.Set;

@Builder
public record UserDto(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        Set<UserRole> roles,
        List<AddressDto> addresses
) {}