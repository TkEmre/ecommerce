package com.tkemre.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // AccessDeniedException import'u
import org.springframework.web.bind.MethodArgumentNotValidException; // Validasyon hataları için
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice // Bu anotasyon, bu sınıfın tüm @Controller'lar için istisna yakalayacağını belirtir
public class GlobalExceptionHandler {

    // 403 Forbidden hatalarını yakalar (Spring Security AccessDeniedException)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        // Loglama yapabilirsiniz: System.err.println("Access Denied: " + ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
                "Bu kaynağa erişim yetkiniz bulunmamaktadır.", // Kullanıcıya gösterilecek mesaj
                HttpStatus.FORBIDDEN
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN); // 403 Forbidden yanıtı döndür
    }

    // @Valid anotasyonu ile tetiklenen validasyon hatalarını yakalar (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.of(
                "Validasyon hatası: " + errorMessage,
                HttpStatus.BAD_REQUEST
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 Bad Request yanıtı döndür
    }

    // Genel tüm diğer istisnaları yakalar (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        // Loglama yapabilirsiniz: ex.printStackTrace();
        ErrorResponse errorResponse = ErrorResponse.of(
                "Beklenmeyen bir hata oluştu: " + ex.getMessage(), // Hatanın detayını göster
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error yanıtı döndür
    }

}
