package com.tkemre.ecommerce.repository;

import com.tkemre.ecommerce.model.Order; // Kendi modeliniz
import com.tkemre.ecommerce.model.User; // Kendi modeliniz
import org.springframework.data.domain.Page; // Sayfalama için
import org.springframework.data.domain.Pageable; // Sayfalama için
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import com.tkemre.ecommerce.model.OrderItem;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {


    List<OrderItem> findAllByOrderId(Long orderId);


}