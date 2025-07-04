package com.tkemre.ecommerce.model;

public enum OrderStatus {
    PENDING,        // Beklemede
    PROCESSING,     // Hazırlanıyor / İşleniyor
    SHIPPED,        // Kargolandı
    DELIVERED,      // Teslim Edildi
    CANCELED,       // İptal Edildi
    RETURNED        // İade Edildi
}