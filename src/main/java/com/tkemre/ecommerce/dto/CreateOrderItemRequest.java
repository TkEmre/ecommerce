package com.tkemre.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

// Bir sipariş kalemi oluşturma isteği için DTO
@Builder
public record CreateOrderItemRequest(
        @NotNull(message = "Product ID cannot be null")
        Long productId, // Sipariş edilecek ürünün ID'si
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity // Sipariş edilecek ürünün miktarı
) {}
