package com.tkemre.ecommerce.controller;

import com.tkemre.ecommerce.dto.CreateProductRequest;
import com.tkemre.ecommerce.dto.ProductDto;
import com.tkemre.ecommerce.dto.UpdateProductRequest;
import com.tkemre.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Yetkilendirme için
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/products") // Ürün işlemleri için base path
@SecurityRequirement(name="bearerAuth")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Yeni ürün oluşturma (Sadece ADMIN rolüne sahip kullanıcılar için)
    // POST /api/v1/products
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Bu endpoint'e sadece ADMIN rolü erişebilir
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto createdProduct = productService.createProduct(request);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // 201 Created döndür
    }

    // Tüm ürünleri listeleme (Herkes erişebilir)
    // GET /api/v1/products?page=0&size=10&sort=name,asc
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        // Sıralama parametrelerini işle
        Sort sorting = Sort.by(sort[0]);
        if (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) {
            sorting = sorting.descending();
        } else {
            sorting = sorting.ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products); // 200 OK döndür
    }

    // ID'ye göre ürün getirme (Herkes erişebilir)
    // GET /api/v1/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product); // 200 OK döndür
    }

    // Kategoriye göre ürünleri listeleme (Herkes erişebilir)
    // GET /api/v1/products/category/{categoryName}?page=0&size=10
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort sorting = Sort.by(sort[0]);
        if (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) {
            sorting = sorting.descending();
        } else {
            sorting = sorting.ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<ProductDto> products = productService.getProductsByCategory(categoryName, pageable);
        return ResponseEntity.ok(products);
    }

    // Ürün güncelleme (Sadece ADMIN rolüne sahip kullanıcılar için)
    // PUT /api/v1/products/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        ProductDto updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct); // 200 OK döndür
    }

    // Ürün silme (Sadece ADMIN rolüne sahip kullanıcılar için)
    // DELETE /api/v1/products/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content döndür
    }

    // Ürün stoğunu güncelleme (Sadece ADMIN rolüne sahip kullanıcılar için)
    // PATCH /api/v1/products/{id}/stock?quantity=5
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProductStock(@PathVariable Long id, @RequestParam Integer quantity) {
        ProductDto updatedProduct = productService.updateProductStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }
}