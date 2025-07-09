package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.dto.CreateProductRequest;
import com.tkemre.ecommerce.dto.ProductDto;
import com.tkemre.ecommerce.dto.UpdateProductRequest;
import com.tkemre.ecommerce.exception.ProductAlreadyExistsException;
import com.tkemre.ecommerce.exception.ProductNotFoundException;
import com.tkemre.ecommerce.model.Product;
import com.tkemre.ecommerce.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct_successful() {
        CreateProductRequest request = new CreateProductRequest("Product A", "Category A", BigDecimal.valueOf(100), 10);

        when(productRepository.findByNameContainingIgnoreCase("Product A")).thenReturn(Collections.emptyList());

        Product savedProduct = Product.builder()
                .id(1L)
                .name(request.name())
                .category(request.category())
                .price(request.price())
                .stock(request.stock())
                .active(true)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDto result = productService.createProduct(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Product A");
    }

    @Test
    void createProduct_alreadyExists_throwsException() {
        CreateProductRequest request = new CreateProductRequest("Product A", "Category A", BigDecimal.TEN, 10);

        Product existing = Product.builder().name("Product A").build();
        when(productRepository.findByNameContainingIgnoreCase("Product A")).thenReturn(List.of(existing));

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(ProductAlreadyExistsException.class);
    }

    @Test
    void getProductById_successful() {
        Product product = Product.builder().id(1L).name("Product A").category("Category A").price(BigDecimal.TEN).stock(5).active(true).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDto result = productService.getProductById(1L);

        assertThat(result.name()).isEqualTo("Product A");
    }

    @Test
    void getProductById_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(1L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void getAllProducts_returnsPagedList() {
        Product product = Product.builder().id(1L).name("Product A").category("Category A").price(BigDecimal.TEN).stock(5).active(true).build();
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ProductDto> result = productService.getAllProducts(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getProductsByCategory_returnsPagedList() {
        Product product = Product.builder().id(1L).name("Product A").category("Electronics").price(BigDecimal.TEN).stock(5).active(true).build();
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAllByCategoryAndActiveTrue(eq("Electronics"), any(Pageable.class))).thenReturn(page);

        Page<ProductDto> result = productService.getProductsByCategory("Electronics", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void updateProduct_successful() {
        Product existing = Product.builder().id(1L).name("Old Name").category("Cat").price(BigDecimal.TEN).stock(3).active(true).build();
        UpdateProductRequest request = new UpdateProductRequest("New Name", "Cat", BigDecimal.valueOf(50), 15, true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.findByNameContainingIgnoreCase("New Name")).thenReturn(Collections.emptyList());
        when(productRepository.save(any(Product.class))).thenReturn(existing);

        ProductDto result = productService.updateProduct(1L, request);

        assertThat(result.name()).isEqualTo("New Name");
    }

    @Test
    void updateProduct_nameConflict_throwsException() {
        Product existing = Product.builder().id(1L).name("Old Name").build();
        Product other = Product.builder().name("New Name").build();
        UpdateProductRequest request = new UpdateProductRequest("New Name", "Cat", BigDecimal.TEN, 5, true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.findByNameContainingIgnoreCase("New Name")).thenReturn(List.of(other));

        assertThatThrownBy(() -> productService.updateProduct(1L, request))
                .isInstanceOf(ProductAlreadyExistsException.class);
    }

    @Test
    void deleteProduct_successful() {
        when(productRepository.existsById(1L)).thenReturn(true);
        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_notFound_throwsException() {
        when(productRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void updateProductStock_successful() {
        Product product = Product.builder().id(1L).stock(10).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.updateProductStock(1L, 20);

        assertThat(result.stock()).isEqualTo(20);
    }

    @Test
    void updateProductStock_negativeQuantity_throwsException() {
        Product product = Product.builder().id(1L).stock(10).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProductStock(1L, -5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    public static class FakeAuthentication implements Authentication {

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public Object getCredentials() {
            return "credentials";
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return "emre@example.com";
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

        @Override
        public String getName() {
            return "emre@example.com";
        }
    }
}
