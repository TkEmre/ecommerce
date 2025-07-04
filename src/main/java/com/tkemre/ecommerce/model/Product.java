package com.tkemre.ecommerce.model;

import jakarta.persistence.*; // @Column ve @Table için import ekle
import lombok.AllArgsConstructor;
import lombok.Builder; // @Builder anotasyonunu kullanmak için import ekle
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String category; // Ürün kategorisi

    @Column(nullable = false)
    private BigDecimal price; // Ürün fiyatı

    @Column(nullable = false)
    private Integer stock; // Stok miktarı

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true; // Ürünün aktif olup olmadığını belirtir (varsayılan olarak aktif).
}