package com.tkemre.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409 Conflict
public class OutOfStockException extends RuntimeException {

  public OutOfStockException(String productName) {
    super("Product " + productName + " is out of stock");
  }

  public OutOfStockException(String productName, int requested, int available) {
    super("Requested quantity (" + requested + ") exceeds available stock (" + available + ") for product: " + productName);
  }
}
