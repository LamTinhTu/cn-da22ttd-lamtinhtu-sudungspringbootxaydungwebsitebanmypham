package com.oceanbutterflyshop.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.oceanbutterflyshop.backend.filters.JwtAuthenticationFilter;

/**
 * Security configuration for JWT-based authentication and authorization.
 * Implements stateless session management with JWT tokens.
 * 
 * Security Features:
 * - JWT token-based authentication
 * - Stateless session management (no cookies)
 * - Role-based access control (ADMIN, STAFF, CUSTOMER)
 * - Public endpoints for authentication and product browsing
 * - Protected endpoints for authenticated users
 * 
 * Authentication Flow:
 * 1. User logs in with credentials → receives JWT token
 * 2. Client includes token in Authorization header: "Bearer <token>"
 * 3. JwtAuthenticationFilter validates token on each request
 * 4. SecurityContext is populated with authenticated user
 * 5. Spring Security enforces role-based access control
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Cấu hình Security Filter Chain với xác thực JWT và phân quyền vai trò.
     * 
     * Các endpoint công khai (Không yêu cầu xác thực):
     * - /api/v1/auth/** - Đăng nhập và đăng ký
     * - /api/v1/products/** (chỉ GET) - Duyệt sản phẩm
     * - /swagger-ui/** - Tài liệu Swagger
     * - /v3/api-docs/** - Tài liệu OpenAPI
     * 
     * Các endpoint được bảo vệ (Yêu cầu xác thực):
     * - Tất cả các endpoint khác yêu cầu token JWT hợp lệ
     * 
     * @param http Cấu hình HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception  cấu hình thất bại
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Vô hiệu hóa CSRF (không cần thiết cho xác thực JWT không trạng thái)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Bật CORS với cấu hình mặc định (sử dụng WebConfig)
            .cors(cors -> cors.configure(http))
            
            // Cấu hình quy tắc phân quyền
            .authorizeHttpRequests(auth -> auth
                // Cho phép tất cả OPTIONS requests (CORS preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Các endpoint công khai - không yêu cầu xác thực
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()  // Chỉ GET được phép cho khách
                .requestMatchers(HttpMethod.GET, "/api/v1/brands/**").permitAll()    // Chỉ GET được phép cho khách
                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()   // Chỉ GET được phép cho khách
                .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()    // Chỉ GET được phép cho khách
                .requestMatchers("/uploads/**").permitAll()  // Cho phép truy cập file tĩnh
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**", "/api-docs/**").permitAll()
                
                // Tất cả các endpoint khác yêu cầu xác thực
                .anyRequest().authenticated()
            )
            
            // Cấu hình quản lý phiên không trạng thái (chỉ sử dụng token JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Cấu hình authentication provider
            .authenticationProvider(authenticationProvider())
            
            // Thêm bộ lọc JWT trước UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Vô hiệu hóa form login mặc định và HTTP basic auth
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);
        
        return http.build();
    }

    /**
     * Cấu hình authentication provider với UserDetailsService và PasswordEncoder
     * 
     * @return AuthenticationProvider được cấu hình với DAO authentication
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Xuất AuthenticationManager bean cho xác thực thủ công
     * Được sử dụng trong AuthController cho chức năng đăng nhập
     * 
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception nếu không thể tạo authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Cấu hình bộ mã hóa mật khẩu sử dụng BCrypt
     * BCrypt là thuật toán băm một chiều có salt
     * 
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

