package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.dto.CreateOrderRequest;
import com.tkemre.ecommerce.dto.OrderDto;
import com.tkemre.ecommerce.dto.UpdateOrderStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Sipariş işlemleri için servis arayüzü
public interface OrderService {
    OrderDto createOrder(String username, CreateOrderRequest request);
    OrderDto getOrderById(Long orderId, String username);
    Page<OrderDto> getUserOrders(String username, Pageable pageable);
    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
    void cancelOrder(Long orderId, String username);
    void deleteOrder(Long orderId);
}
