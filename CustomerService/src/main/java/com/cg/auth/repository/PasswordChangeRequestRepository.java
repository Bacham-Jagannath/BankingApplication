package com.cg.auth.repository;

import com.cg.auth.entity.PasswordChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordChangeRequestRepository extends JpaRepository<PasswordChangeRequest,Long> {

    @Query("select p from PasswordChangeRequest p where p.panNumber =:panNumber")
    public Optional<PasswordChangeRequest> findByPanNumber(@Param("panNumber") String panNumber);

    @Query("select p from PasswordChangeRequest p where p.status = :status")
    public Optional<PasswordChangeRequest> getPasswordChangeRequestByStatus(@Param("status") String status);

    public List<PasswordChangeRequest> findAllByStatus(String status);

}
