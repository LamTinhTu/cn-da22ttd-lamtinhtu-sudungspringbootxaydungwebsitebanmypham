package com.oceanbutterflyshop.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ocean Butterfly Shop API")
                        .version("1.0.0")
                        .description("RESTful API documentation for the Ocean Butterfly Shop Management System. Supports CRUD operations for products, brands, users, and orders with auto-generated codes and BigDecimal precision.")
                        .contact(new Contact()
                                .name("Ocean Butterfly Shop Team")
                                .email("support@oceanbutterflyshop.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
