package com.oceanbutterflyshop.backend.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

import com.oceanbutterflyshop.backend.enums.Gender;

import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String userCode;
    private String userName;
    
    @Enumerated(EnumType.STRING)
    private Gender userGender;
    
    private LocalDate userBirthDate;
    private String userAddress;
    private String userPhone;
    private String userAccount;
    private String userPassword;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "role_id") // Khóa ngoại
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}
