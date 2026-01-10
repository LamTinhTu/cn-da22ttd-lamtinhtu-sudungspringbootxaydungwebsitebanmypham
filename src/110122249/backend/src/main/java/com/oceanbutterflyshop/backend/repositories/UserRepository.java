package com.oceanbutterflyshop.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oceanbutterflyshop.backend.entities.User;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserAccount(String userAccount);
    Optional<User> findByUserCode(String userCode);
    Optional<User> findByUserPhone(String userPhone);
    boolean existsByUserAccount(String userAccount);
    boolean existsByUserPhone(String userPhone);
    boolean existsByUserCode(String userCode);
    List<User> findByRoleRoleCode(String roleCode);
    List<User> findByRole_RoleCode(String roleCode);

    @Query("SELECT u FROM User u WHERE " +
           "(:roleName IS NULL OR u.role.roleName = :roleName) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "u.userPhone LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(u.userCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.userAccount) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsers(@Param("keyword") String keyword, @Param("roleName") String roleName, Pageable pageable);
}