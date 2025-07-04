package com.tkemre.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkemre.ecommerce.dto.CreateProductRequest;
import com.tkemre.ecommerce.model.Product; // Import Product sınıfı
import com.tkemre.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                .build();

        // Her testten önce veritabanını temizle
        productRepository.deleteAll();

        Product product1 = Product.builder()
                .name("Laptop")
                .category("Electronics")
                .price(new BigDecimal("1200.00"))
                .stock(10)
                .active(true)
                .build();

        Product product2 = Product.builder()
                .name("Mouse")
                .category("Accessories")
                .price(new BigDecimal("50.00"))
                .stock(25)
                .active(true)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testCreateProduct_shouldReturnProductDto_whenRequestIsValid() throws Exception {
        CreateProductRequest request = new CreateProductRequest("New Product", "Books", new BigDecimal("100.00"), 5);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.category").value("Books"))
                .andExpect(jsonPath("$.price").value(100.00));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void testGetAllProducts_shouldReturnListOfProductDto() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                .andExpect(jsonPath("$.content[0].category").value("Electronics"))
                .andExpect(jsonPath("$.content[1].name").value("Mouse"))
                .andExpect(jsonPath("$.content[1].category").value("Accessories"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void testGetProductById_shouldReturnProductDto_whenProductExists() throws Exception {
        Product product = productRepository.findByName("Laptop").orElseThrow();

        mockMvc.perform(get("/api/v1/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.category").value("Electronics")) // Kategori kontrolü
                .andExpect(jsonPath("$.price").value(1200.00));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void testGetProductById_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testUpdateProductStock_shouldUpdateStock_whenProductExists() throws Exception {
        Product product = productRepository.findByName("Mouse").orElseThrow();

        mockMvc.perform(patch("/api/v1/products/{id}/stock", product.getId())
                        .param("quantity", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(30));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testDeleteProduct_shouldReturnNoContent_whenProductExists() throws Exception {
        Product product = productRepository.findByName("Laptop").orElseThrow();

        mockMvc.perform(delete("/api/v1/products/{id}", product.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", product.getId())
                        .with(user("test_user").roles("USER")))
                .andExpect(status().isNotFound());
    }
}