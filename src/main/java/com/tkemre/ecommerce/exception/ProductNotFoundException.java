package com.tkemre.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // HTTP 404 Not Found durum kodu döndürür
public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(String message) {
    super("Product not found: " + message);
  }
}