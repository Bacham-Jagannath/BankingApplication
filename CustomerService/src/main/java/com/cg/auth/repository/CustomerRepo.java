package com.cg.auth.repository;

import com.cg.auth.entity.Customer;
import com.cg.auth.exception.AuthServiceException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer,String> {

    public Optional<Customer> findByPanNumber(String panNumber);


    @Query("select c from Customer c where c.status=:status")
    public List<Customer> getCustomerByRegistrationStatus(@Param("status") String status);
}
