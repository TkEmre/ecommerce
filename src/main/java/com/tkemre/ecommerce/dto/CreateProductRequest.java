package com.tkemre.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin; // Fiyat için minimum değer
import jakarta.validation.constraints.Min; // Stok için minimum değer
import jakarta.validation.constraints.NotBlank; // Boş olamaz
import jakarta.validation.constraints.NotNull; // Null olamaz
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record CreateProductRequest(
        @NotBlank(message = "Product name cannot be blank")
        String name,
        @NotBlank(message = "Category cannot be blank")
        String category,
        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0") // Fiyat 0'dan büyük olmalı
        BigDecimal price,
        @NotNull(message = "Stock cannot be null")
        @Min(value = 0, message = "Stock cannot be negative") // Stok negatif olamaz
        Integer stock
) {}