package com.tkemre.ecommerce.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

// API'den dönen hata yanıtlarının yapısı için DTO
@Builder
public record ErrorResponse(
        String message,         // Hatanın açıklayıcı mesajı
        int status,             // HTTP durum kodu (örn: 403)
        String error,           // HTTP durum kodunun metin açıklaması (örn: "Forbidden")
        LocalDateTime timestamp // Hatanın oluştuğu zaman
) {
    // Statik fabrika metodu, kolayca ErrorResponse objesi oluşturmak için
    public static ErrorResponse of(String message, HttpStatus status) {
        return ErrorResponse.builder()
                .message(message)
                .status(status.value())
                .error(status.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
