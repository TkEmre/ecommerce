package com.tkemre.ecommerce.dto;

import com.tkemre.ecommerce.model.OrderStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record OrderDto(
        Long id, // Siparişin kendi ID'si
        Long userId, // Siparişi veren kullanıcının ID'si
        String username, // Siparişi veren kullanıcının kullanıcı adı (kolaylık için)
        AddressDto shippingAddress, // Teslimat adresi detayları (AddressDto olarak)
        Set<OrderItemDto> orderItems, // Sipariş içerisindeki tüm ürün kalemleri
        LocalDateTime orderDate, // Siparişin verildiği tarih
        BigDecimal totalPrice, // Siparişin toplam tutarı
        OrderStatus status // Siparişin mevcut durumu
) {}