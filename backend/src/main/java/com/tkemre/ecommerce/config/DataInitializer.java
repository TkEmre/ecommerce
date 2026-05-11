package com.tkemre.ecommerce.config;

import com.tkemre.ecommerce.model.Product;
import com.tkemre.ecommerce.model.User;
import com.tkemre.ecommerce.model.UserRole;
import com.tkemre.ecommerce.repository.ProductRepository;
import com.tkemre.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedProducts();
    }

    private void seedAdmin() {
        String adminUsername = "admin@store.com";
        if (userRepository.existsByUsername(adminUsername)) return;

        User admin = User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode("admin123"))
                .email(adminUsername)
                .firstName("Store")
                .lastName("Admin")
                .roles(Set.of(UserRole.ADMIN, UserRole.USER))
                .addresses(new HashSet<>())
                .build();

        userRepository.save(admin);
        System.out.println("✓ Admin account created: " + adminUsername + " / admin123");
    }

    private void seedProducts() {
        if (productRepository.count() > 0) return;

        List<Product> products = List.of(
            Product.builder().name("iPhone 15 Pro").category("electronics")
                .price(new BigDecimal("999.00")).stock(42).active(true).build(),
            Product.builder().name("Samsung Galaxy S24").category("electronics")
                .price(new BigDecimal("849.00")).stock(35).active(true).build(),
            Product.builder().name("MacBook Air M3").category("electronics")
                .price(new BigDecimal("1299.00")).stock(18).active(true).build(),
            Product.builder().name("Sony WH-1000XM5").category("electronics")
                .price(new BigDecimal("349.00")).stock(60).active(true).build(),
            Product.builder().name("iPad Pro 11\"").category("electronics")
                .price(new BigDecimal("799.00")).stock(25).active(true).build(),
            Product.builder().name("Nike Air Max 270").category("clothing")
                .price(new BigDecimal("129.00")).stock(80).active(true).build(),
            Product.builder().name("Levi's 501 Jeans").category("clothing")
                .price(new BigDecimal("89.00")).stock(120).active(true).build(),
            Product.builder().name("Adidas Ultraboost 23").category("clothing")
                .price(new BigDecimal("179.00")).stock(55).active(true).build(),
            Product.builder().name("The Pragmatic Programmer").category("books")
                .price(new BigDecimal("39.99")).stock(200).active(true).build(),
            Product.builder().name("Clean Code").category("books")
                .price(new BigDecimal("34.99")).stock(180).active(true).build(),
            Product.builder().name("Designing Data-Intensive Applications").category("books")
                .price(new BigDecimal("49.99")).stock(95).active(true).build(),
            Product.builder().name("Dyson V15 Detect").category("home")
                .price(new BigDecimal("699.00")).stock(22).active(true).build(),
            Product.builder().name("Nespresso Vertuo Pop").category("home")
                .price(new BigDecimal("99.00")).stock(75).active(true).build(),
            Product.builder().name("Instant Pot Duo 7-in-1").category("home")
                .price(new BigDecimal("89.99")).stock(110).active(true).build(),
            Product.builder().name("IKEA KALLAX Shelf").category("home")
                .price(new BigDecimal("59.99")).stock(40).active(true).build()
        );

        productRepository.saveAll(products);
        System.out.println("✓ " + products.size() + " products seeded.");
    }
}
