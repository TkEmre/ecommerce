package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.dto.CreateProductRequest;
import com.tkemre.ecommerce.dto.ProductDto;
import com.tkemre.ecommerce.dto.UpdateProductRequest;
import com.tkemre.ecommerce.exception.ProductNotFoundException; // Yeni exception (henüz oluşturmadıysak)
import com.tkemre.ecommerce.exception.ProductAlreadyExistsException; // Yeni exception (henüz oluşturmadıysak)
import com.tkemre.ecommerce.model.Product;
import com.tkemre.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto createProduct(CreateProductRequest request) {
        // Ürün adının benzersizliğini kontrol et
        if (productRepository.findByNameContainingIgnoreCase(request.name()).stream().anyMatch(p -> p.getName().equalsIgnoreCase(request.name()))) {
            throw new ProductAlreadyExistsException("Product with name " + request.name() + " already exists.");
        }

        Product product = Product.builder()
                .name(request.name())
                .category(request.category())
                .price(request.price())
                .stock(request.stock())
                .active(true) // Yeni ürün varsayılan olarak aktif başlar
                .build();

        Product savedProduct = productRepository.save(product);
        return toProductDto(savedProduct);
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return toProductDto(product);
    }

    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toProductDto); // Her Product'ı ProductDto'ya dönüştür
    }

    @Override
    public Page<ProductDto> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findAllByCategoryAndActiveTrue(category, pageable) // Sadece aktif ürünleri getir
                .map(this::toProductDto);
    }

    @Override
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Ürün adının güncellenirken başka bir ürünle çakışmamasını kontrol et
        if (!product.getName().equalsIgnoreCase(request.name()) && productRepository.findByNameContainingIgnoreCase(request.name()).stream().anyMatch(p -> p.getName().equalsIgnoreCase(request.name()))) {
            throw new ProductAlreadyExistsException("Product with name " + request.name() + " already exists.");
        }

        product.setName(request.name());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setActive(request.active());

        Product updatedProduct = productRepository.save(product);
        return toProductDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        // Silmeden önce ürünün var olup olmadığını kontrol et
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductDto updateProductStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Stok miktarının geçerliliğini kontrol et
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }

        product.setStock(quantity);
        Product updatedProduct = productRepository.save(product);
        return toProductDto(updatedProduct);
    }

    // Product entity'sinden ProductDto'ya dönüşüm metodu
    private ProductDto toProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .active(product.getActive())
                .build();
    }
}