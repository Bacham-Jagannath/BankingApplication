package com.cg.auth.repository;

import com.cg.auth.dto.AdminDto;
import com.cg.auth.dto.AuthResponse;
import com.cg.auth.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    public Optional<Admin> findByUsernameAndPassword(String username, String password);
    public Optional<Admin> findByUsername(String username);
}
