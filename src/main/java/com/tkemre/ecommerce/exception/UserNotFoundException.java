package com.tkemre.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // HTTP 404 Not Found durum kodu döner
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("User Not Found: " + message);
    }
}