package com.oceanbutterflyshop.backend.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oceanbutterflyshop.backend.enums.Gender;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    
    @Column(name = "user_code", unique = true, nullable = false, length = 10)
    private String userCode;
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_gender")
    private Gender userGender;
    
    @Column(name = "user_birth_date")
    private LocalDate userBirthDate;
    
    @Column(name = "user_address")
    private String userAddress;
    
    @Column(name = "user_phone", length = 11)
    private String userPhone;
    
    @Column(name = "user_account", unique = true, nullable = false)
    private String userAccount;
    
    @Column(name = "user_password", nullable = false, length = 255)
    private String userPassword;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "role_id") // Khóa ngoại
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;
}
