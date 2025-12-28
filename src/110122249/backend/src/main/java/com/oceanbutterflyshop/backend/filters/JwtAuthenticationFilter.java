package com.oceanbutterflyshop.backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.oceanbutterflyshop.backend.utils.JwtUtils;

import java.io.IOException;

/**
 * JWT Authentication Filter hoạt động để intercept các yêu cầu HTTP và xác thực các token JWT.
 * Mở rộng OncePerRequestFilter để đảm bảo thực thi một lần cho mỗi yêu cầu.
 * 
 * Quy trình xác thực JWT:
 * 1. Trích xuất header 'Authorization' từ yêu cầu
 * 2. Kiểm tra xem header có chứa tiền tố 'Bearer ' hay không
 * 3. Trích xuất token JWT từ header
 * 4. Xác thực token và trích xuất tên người dùng
 * 5. Tải thông tin người dùng từ cơ sở dữ liệu
 * 6. Tạo đối tượng authentication và đặt vào SecurityContext
 * 
 * Định dạng Token: "Authorization: Bearer <JWT_TOKEN>"
 * 
 * Filter chạy trước UsernamePasswordAuthenticationFilter trong chuỗi bảo mật.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    /**
     * Lọc các yêu cầu đến để xác thực token JWT
     * 
     * @param request Yêu cầu HTTP
     * @param response Phản hồi HTTP
     * @param filterChain Chuỗi bộ lọc để tiếp tục xử lý
     * @throws ServletException nếu xảy ra lỗi servlet
     * @throws IOException nếu xảy ra lỗi I/O
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Trích xuất header Authorization
        final String authorizationHeader = request.getHeader("Authorization");
        
        // Kiểm tra xem header Authorization có tồn tại và có tiền tố Bearer hay không
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // Trích xuất token JWT (loại bỏ tiền tố "Bearer ")
            final String jwt = authorizationHeader.substring(7);
            final String username = jwtUtils.extractUsername(jwt);
            
            log.debug("JWT token found for username: {}", username);
            
            // Nếu tên người dùng tồn tại và người dùng chưa được xác thực
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Tải thông tin người dùng từ cơ sở dữ liệu
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Xác thực token với thông tin người dùng
                if (jwtUtils.validateToken(jwt, userDetails)) {
                    log.debug("JWT token is valid for user: {}", username);
                    
                    // Tạo authentication token
                    UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    
                    // Đặt chi tiết xác thực từ yêu cầu
                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Đặt xác thực vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    log.debug("User {} authenticated successfully with roles: {}", 
                        username, userDetails.getAuthorities());
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        
        // Tiếp tục chuỗi bộ lọc
        filterChain.doFilter(request, response);
    }
}
