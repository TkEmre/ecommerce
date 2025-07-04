package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.dto.CreateProductRequest;
import com.tkemre.ecommerce.dto.ProductDto;
import com.tkemre.ecommerce.dto.UpdateProductRequest;
import org.springframework.data.domain.Page; // Sayfalama için Page
import org.springframework.data.domain.Pageable; // Sayfalama için Pageable

import java.util.List;

public interface ProductService {
    ProductDto createProduct(CreateProductRequest request);
    ProductDto getProductById(Long id);
    Page<ProductDto> getAllProducts(Pageable pageable); // Tüm ürünleri sayfalı getir
    Page<ProductDto> getProductsByCategory(String category, Pageable pageable); // Kategoriye göre ürünleri sayfalı getir
    ProductDto updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);
    // Stok güncelleme gibi ek metotlar daha sonra eklenebilir
    ProductDto updateProductStock(Long id, Integer quantity); // Stok güncelleme metodu
}