package com.tkemre.ecommerce.dto;

import com.tkemre.ecommerce.model.OrderStatus; // OrderStatus enum'ını kullanmak için
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateOrderStatusRequest(
        @NotNull(message = "Order status cannot be null")
        OrderStatus newStatus
) {}