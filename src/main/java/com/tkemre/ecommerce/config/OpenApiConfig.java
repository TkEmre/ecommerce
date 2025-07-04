// src/main/java/com/tkemre/ecommerce/config/OpenApiConfig.java

package com.tkemre.ecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "E-commerce API", version = "1.0", description = "E-ticaret uygulaması için API dokümantasyonu"))
@SecurityScheme(
        name = "bearerAuth", // Güvenlik şemasının adı
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

}