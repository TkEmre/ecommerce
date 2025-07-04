package com.tkemre.ecommerce.repository;

import com.tkemre.ecommerce.model.Order;
import com.tkemre.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Belirli bir kullanıcıya ait tüm siparişleri getirme (sayfalama ile)
    Page<Order> findAllByUser(User user, Pageable pageable);

    // Belirli bir kullanıcıya ait ve belirli bir sipariş ID'sine sahip siparişi getirme
    // Kullanıcının sadece kendi siparişlerini görmesini sağlamak için önemli
    Optional<Order> findByIdAndUser(Long id, User user);


}