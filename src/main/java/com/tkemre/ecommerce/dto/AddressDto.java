

package com.tkemre.ecommerce.dto;

import lombok.Builder;
import java.io.Serializable;

@Builder
public record AddressDto(
        Long id,
        String street,
        String city,
        String state,
        String postalCode,
        String country,
        Boolean isDefault
) implements Serializable { }