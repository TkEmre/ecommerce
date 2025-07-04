package com.tkemre.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

// Yeni bir sipariş oluşturma isteği için DTO
@Builder
public record CreateOrderRequest(
        @NotNull(message = "Shipping address ID cannot be null")
        Long shippingAddressId, // Siparişin teslimat adresi ID'si
        @NotEmpty(message = "Order must contain at least one item")
        List<CreateOrderItemRequest> items // Sipariş edilecek ürün kalemlerinin listesi
) {}
