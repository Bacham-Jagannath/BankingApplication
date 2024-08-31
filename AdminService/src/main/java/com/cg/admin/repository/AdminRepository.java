package com.cg.admin.repository;

import com.cg.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    public Optional<Admin> findByUsernameAndPassword(String username, String password);
    public Optional<Admin> findByUsername(String username);

}
