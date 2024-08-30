package com.cg.auth.repository;

import com.cg.auth.entity.PassWordChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassWordRepository extends JpaRepository<PassWordChangeRequest,Long> {

    @Query("select p from PassWordRequest p where p.customer =:panNumber")
    public Optional<PassWordChangeRequest> findByPanNumber(String panNumber);

    @Query("select p from PassWordRequest p where p.passwordReqStatus = :passwordReqStatus")
    public Optional<PassWordChangeRequest> getByPassWordReqByStatus(String passwordReqStatus);

}
