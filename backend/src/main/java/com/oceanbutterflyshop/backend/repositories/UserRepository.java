package com.oceanbutterflyshop.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oceanbutterflyshop.backend.entities.User;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserAccount(String userAccount);
    Optional<User> findByUserCode(String userCode);
    boolean existsByUserAccount(String userAccount);
    boolean existsByUserPhone(String userPhone);
    boolean existsByUserCode(String userCode);
    List<User> findByRoleRoleCode(String roleCode);
}