package com.tkemre.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record UpdateProductRequest(

        @NotBlank(message = "Product name cannot be blank")
        String name,
        @NotBlank(message = "Category cannot be blank")
        String category,
        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,
        @NotNull(message = "Stock cannot be null")
        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock,
        @NotNull(message = "Active status cannot be null")
        Boolean active
) {}