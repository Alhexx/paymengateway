package com.challenge.paymengateway.application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.challenge.paymengateway.application.model.Billing;
import com.challenge.paymengateway.common.enums.StatusCobranca;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Integer> {
  Optional<Billing> findById(Integer id);

  @Query("""
           SELECT b FROM Billing b 
           WHERE b.sender.id = :senderId
           AND (:status IS NULL OR b.status = :status)
        """)
  Optional<List<Billing>> findBySenderIdAndStatusOptional(@Param("senderId") Integer senderId, @Param("status") StatusCobranca status);

  @Query("""
           SELECT b FROM Billing b 
           WHERE b.reciever.id = :recieverId
           AND (:status IS NULL OR b.status = :status)
        """)
  Optional<List<Billing>> findByRecieverIdAndStatusOptional(@Param("recieverId") Integer recieverId, @Param("status") StatusCobranca status);
}
