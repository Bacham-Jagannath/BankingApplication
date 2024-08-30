package com.cg.auth.repository;

import com.cg.auth.dto.CustomerDto;
import com.cg.auth.entity.Customer;
import com.cg.auth.exception.AuthServiceException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer,String> {

    Customer findByUsernameAndPassword(String username, String password) throws AuthServiceException;

    public Optional<Customer> findByUsername(String username);

    public Optional<Customer> findByPanNumber(String panNumber);

    @Query("select c from Customer c where c.registrationReqStatus=:registrationReqStatus")
    public List<Customer> getCustomerByRegistrationStatus(@Param("registrationReqStatus") String registrationReqStatus);


}
