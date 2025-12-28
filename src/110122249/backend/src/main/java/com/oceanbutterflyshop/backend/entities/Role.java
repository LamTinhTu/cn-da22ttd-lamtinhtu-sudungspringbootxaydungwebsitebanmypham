package com.oceanbutterflyshop.backend.entities;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;
    
    @Column(name = "role_code", unique = true, nullable = false)
    private String roleCode;
    
    @Column(name = "role_name", nullable = false)
    private String roleName;

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private List<User> users;
}
