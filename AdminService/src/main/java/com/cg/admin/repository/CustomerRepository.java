package com.cg.admin.repository;

import com.cg.admin.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,String> {
    Customer findByPanNumber(String panNumber);

}
