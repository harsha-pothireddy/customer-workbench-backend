package com.customersaas.workbench.repository;

import com.customersaas.workbench.entity.CustomerInteraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CustomerInteractionRepository extends JpaRepository<CustomerInteraction, Long> {

    Page<CustomerInteraction> findByCustomerId(String customerId, Pageable pageable);

    Page<CustomerInteraction> findByInteractionType(String interactionType, Pageable pageable);

    Page<CustomerInteraction> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT ci FROM CustomerInteraction ci WHERE " +
           "(:customerId IS NULL OR ci.customerId = :customerId) AND " +
           "(:interactionType IS NULL OR ci.interactionType = :interactionType) AND " +
           "(:startDate IS NULL OR ci.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR ci.timestamp <= :endDate)")
    Page<CustomerInteraction> searchInteractions(
        @Param("customerId") String customerId,
        @Param("interactionType") String interactionType,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
}
