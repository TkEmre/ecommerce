package com.tkemre.ecommerce.repository;

import com.tkemre.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    Page<Product> findAllByCategory(String category, Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String name);

    Page<Product> findAllByActiveTrue(Pageable pageable);

    Page<Product> findAllByCategoryAndActiveTrue(String category, Pageable pageable);
}