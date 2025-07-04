package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.dto.CreateProductRequest;
import com.tkemre.ecommerce.dto.ProductDto;
import com.tkemre.ecommerce.exception.ProductNotFoundException;
import com.tkemre.ecommerce.model.Product;
import com.tkemre.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        reset(productRepository);
    }

    @Test
    void testCreateProduct_shouldReturnProductDto_whenCategoryIsProvided() {

        CreateProductRequest request = CreateProductRequest.builder()
                .name("Test Ürün")

                .price(BigDecimal.valueOf(100.00))
                .stock(10)
                .category("Elektronik")
                .build();

        Product productToSave = Product.builder()
                .name(request.name())
                .price(request.price())
                .stock(request.stock())
                .category(request.category())
                .active(true)
                .build();
        productToSave.setId(1L);

        when(productRepository.save(any(Product.class))).thenReturn(productToSave);

        ProductDto result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Ürün", result.name());
        assertEquals("Elektronik", result.category());
        assertEquals(10, result.stock());
        assertEquals(true, result.active());



        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProductStock_shouldUpdateStock_whenProductExists() {
        Long productId = 1L;
        Integer newQuantity = 15;

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Mevcut Ürün")

                .stock(10)
                .category("Elektronik")
                .active(true)

                .build();

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Mevcut Ürün")

                .stock(newQuantity)
                .category("Elektronik")
                .active(true)
                .build();


        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When:
        ProductDto result = productService.updateProductStock(productId, newQuantity);

        // Then:
        assertNotNull(result);
        assertEquals(newQuantity, result.stock());
        assertEquals(productId, result.id());
        assertEquals("Elektronik", result.category());
        assertEquals(true, result.active());

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProductStock_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        // Given:
        Long nonExistentProductId = 99L;
        Integer quantity = 20;

        // Mock davranışını tanımla:
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When & Then:
        assertThrows(ProductNotFoundException.class, () ->
                productService.updateProductStock(nonExistentProductId, quantity)
        );

        verify(productRepository).findById(nonExistentProductId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testGetProductById_shouldReturnProductDto_whenProductExists() {
        // Given
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("Laptop")
                .price(BigDecimal.valueOf(1200.00))
                .stock(5)
                .category("Elektronik")
                .active(true)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        ProductDto result = productService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.id());
        assertEquals("Laptop", result.name());
        assertEquals("Elektronik", result.category());
        assertEquals(true, result.active());
        verify(productRepository).findById(productId);
    }

    @Test
    void testGetProductById_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        // Given
        Long productId = 99L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
        verify(productRepository).findById(productId);
    }
}