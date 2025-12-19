package com.oceanbutterflyshop.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI/Swagger Configuration for Watch Store API
 * 
 * Features:
 * - JWT Bearer Token Authentication
 * - Global security requirement (no need to unlock each endpoint individually)
 * - Comprehensive API documentation with versioning
 * - Contact and license information
 * 
 * Access Swagger UI at: http://localhost:5000/swagger-ui/index.html
 * Access OpenAPI JSON at: http://localhost:5000/v3/api-docs
 */
@Configuration
public class OpenAPIConfig {
    
    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API Information
                .info(new Info()
                        .title("Ocean Butterfly Shop Management API")
                        .version("v1.0")
                        .description("""
                                # Ocean Butterfly Shop Management System API Documentation
                                
                                RESTful API for managing watch retail operations including:
                                - **Product Management**: CRUD operations for products with brand associations
                                - **Brand Management**: Manage watch brands and manufacturers
                                - **Order Management**: Create and track customer orders with status updates
                                - **User Management**: Admin user management with role-based access control
                                - **Authentication**: JWT-based authentication with role-based authorization
                                
                                ## Authentication
                                This API uses **JWT Bearer Token** authentication. To access protected endpoints:
                                1. Register or login via `/api/v1/auth/register` or `/api/v1/auth/login`
                                2. Copy the `accessToken` from the response
                                3. Click the **Authorize** button (🔓) at the top right
                                4. Enter: `Bearer <your-token>` and click **Authorize**
                                
                                ## Authorization Roles
                                - **ADMIN**: Full system access (User management, Revenue, Settings)
                                - **STAFF**: Order and Product management
                                - **CUSTOMER**: View products, Place orders, View order history
                                - **GUEST**: View products, Register, Login
                                
                                ## API Conventions
                                - All endpoints are prefixed with `/api/v1`
                                - All responses follow standard JSON format: `{status, message, data}`
                                - Auto-generated codes: Brand (TH), Product (SP), Order (DH), User (AD/NV/KH)
                                - BigDecimal precision for all monetary values
                                """)
                        .contact(new Contact()
                                .name("Ocean Butterfly Shop Development Team")
                                .email("support@oceanbutterflyshop.com")
                                .url("https://oceanbutterflyshop.com"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                
                // Server Information
                .addServersItem(new Server()
                        .url("http://localhost:5000")
                        .description("Development Server"))
                .addServersItem(new Server()
                        .url("https://api.watchstore.com")
                        .description("Production Server"))
                
                // Security Configuration
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        Enter JWT Bearer token in the format: `Bearer <token>`
                                        
                                        To obtain a token:
                                        1. POST /api/v1/auth/login with valid credentials
                                        2. Copy the accessToken from response
                                        3. Use it here without the 'Bearer' prefix (will be added automatically)
                                        """)))
                
                // Global Security Requirement (applies to all endpoints by default)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
