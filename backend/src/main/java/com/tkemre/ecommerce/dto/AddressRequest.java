// src/main/java/com/tkemre/ecommerce/dto/AddressRequest.java

package com.tkemre.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank(message = "Street cannot be blank")
        String street,
        @NotBlank(message = "City cannot be blank")
        String city,
        @NotBlank(message = "State cannot be blank")
        String state,
        @NotBlank(message = "Postal Code cannot be blank")
        String postalCode,
        @NotBlank(message = "Country cannot be blank")
        String country
) { }