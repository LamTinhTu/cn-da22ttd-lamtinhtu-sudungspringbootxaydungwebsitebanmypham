package com.oceanbutterflyshop.backend.repositories;

import com.oceanbutterflyshop.backend.entities.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {
    
    Optional<OTPVerification> findTopByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(String phoneNumber);
    
    Optional<OTPVerification> findTopByPhoneNumberAndVerifiedTrueOrderByCreatedAtDesc(String phoneNumber);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    void deleteByPhoneNumber(String phoneNumber);
    
    void deleteByPhoneNumberAndVerifiedFalse(String phoneNumber);
}
