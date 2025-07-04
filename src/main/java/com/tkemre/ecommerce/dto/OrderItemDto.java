package com.tkemre.ecommerce.dto;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record OrderItemDto(
        Long id, // OrderItem'ın kendi ID'si
        Long productId, // Sipariş edilen ürünün ID'si
        String productName, // Sipariş edilen ürünün adı (kolaylık için)
        String productCategory, // Sipariş edilen ürünün kategorisi (kolaylık için)
        Integer quantity, // Sipariş edilen miktar
        BigDecimal priceAtOrder, // Sipariş anındaki birim fiyatı
        BigDecimal totalPrice // Bu sipariş kaleminin toplam fiyatı (quantity * priceAtOrder)
) {}