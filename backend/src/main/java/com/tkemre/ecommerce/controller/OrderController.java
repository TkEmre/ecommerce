package com.tkemre.ecommerce.controller;

import com.tkemre.ecommerce.dto.CreateOrderRequest;
import com.tkemre.ecommerce.dto.OrderDto;
import com.tkemre.ecommerce.dto.UpdateOrderStatusRequest;
import com.tkemre.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Yetkilendirme için
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Mevcut kullanıcıyı almak için
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Yeni sipariş oluştur (Sadece USER veya ADMIN rolüne sahip kullanıcılar için)
    // POST /api/v1/orders
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderDto> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDto createdOrder = orderService.createOrder(userDetails.getUsername(), request);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED); // 201 Created döndür
    }

    // ID'ye göre sipariş detayını getir (Sadece sipariş sahibi kullanıcı veya ADMIN için)
    // GET /api/v1/orders/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> getOrderById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        OrderDto order = orderService.getOrderById(id, userDetails.getUsername());
        return ResponseEntity.ok(order); // 200 OK döndür
    }

    // Oturum açmış kullanıcının siparişlerini listele (Sayfalama ile)
    // GET /api/v1/orders?page=0&size=10&sort=orderDate,desc
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDto>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate,desc") String[] sort) {

        // Sıralama parametrelerini işle
        Sort sorting = Sort.by(sort[0]);
        if (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) {
            sorting = sorting.descending();
        } else {
            sorting = sorting.ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<OrderDto> orders = orderService.getUserOrders(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(orders); // 200 OK döndür
    }

    // Sipariş durumunu güncelle (Sadece ADMIN rolüne sahip kullanıcılar için)
    // PUT /api/v1/orders/{id}/status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderDto updatedOrder = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(updatedOrder);
    }

    // Siparişi iptal et (Sadece sipariş sahibi kullanıcı veya ADMIN için)
    // DELETE /api/v1/orders/{id} - Endpoint tanımınızda bu şekildeydi. Normalde PUT daha uygun olabilir.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        orderService.cancelOrder(id, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content döndür
    }

}