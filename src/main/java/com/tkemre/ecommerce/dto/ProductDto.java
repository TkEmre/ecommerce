package com.tkemre.ecommerce.dto;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record ProductDto(
        Long id,
        String name,
        String category,
        BigDecimal price,
        Integer stock,
        Boolean active
) {}