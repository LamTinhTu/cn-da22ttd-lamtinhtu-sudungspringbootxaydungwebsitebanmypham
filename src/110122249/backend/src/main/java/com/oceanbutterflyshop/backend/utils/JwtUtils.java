package com.oceanbutterflyshop.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Lớp tiện ích JWT để tạo, xác thực và trích xuất token.
 * Xử lý các thao tác JWT cho xác thực và phân quyền.
 * 
 * Cấu trúc Token: Header.Payload.Signature
 * - Header: Thuật toán và loại token
 * - Payload: Claims (username, roles, expiration, v.v.)
 * - Signature: Chữ ký HMAC SHA-256
 * 
 * Cấu hình trong application.properties:
 * - jwt.secret: Khóa bí mật để ký token (tối thiểu 256 bits)
 * - jwt.expiration: Thời gian hết hạn token tính bằng milliseconds (mặc định: 24 giờ)
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;

    /**
     * Trích xuất username (subject) từ JWT token
     * 
     * @param token JWT token
     * @return Username từ token claims
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất ngày hết hạn từ JWT token
     * 
     * @param token JWT token
     * @return Ngày hết hạn
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Trích xuất claim cụ thể từ JWT token
     * 
     * @param token JWT token
     * @param claimsResolver Hàm để trích xuất claim cụ thể
     * @param <T> Kiểu dữ liệu của claim
     * @return Giá trị claim đã trích xuất
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Trích xuất tất cả claims từ JWT token
     * 
     * @param token JWT token
     * @return Tất cả claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Kiểm tra token đã hết hạn hay chưa
     * 
     * @param token JWT token
     * @return true nếu token đã hết hạn, false nếu còn hiệu lực
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Tạo JWT token với username
     * 
     * @param username Username để đưa vào token
     * @return JWT token đã tạo
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Tạo JWT token với các claims tùy chỉnh
     * 
     * @param extraClaims Các claims bổ sung để đưa vào token
     * @param username Username để đưa vào token
     * @return JWT token đã tạo
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return createToken(extraClaims, username);
    }

    /**
     * Tạo JWT token với claims và subject
     * 
     * @param claims Claims để đưa vào token
     * @param subject Subject (username) của token
     * @return Chuỗi JWT token đã tạo
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Xác thực JWT token với thông tin người dùng
     * 
     * @param token JWT token
     * @param userDetails Thông tin người dùng để xác thực
     * @return true nếu token hợp lệ, false nếu không hợp lệ
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Xác thực JWT token (xác thực đơn giản)
     * 
     * @param token JWT token
     * @return true nếu token hợp lệ và chưa hết hạn, false nếu không hợp lệ
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Lấy khóa ký từ secret
     * 
     * @return Khóa bí mật để ký JWT tokens
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
