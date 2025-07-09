package com.mpesa.acquisition.repository;

import com.mpesa.acquisition.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Application a WHERE a.status = :status ORDER BY a.createdAt DESC")
    List<Application> findByStatusOrderByCreatedAtDesc(@Param("status") Application.ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.bankName = :bankName ORDER BY a.createdAt DESC")
    List<Application> findByBankNameOrderByCreatedAtDesc(@Param("bankName") String bankName);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = :status")
    Long countByStatus(@Param("status") Application.ApplicationStatus status);
}
