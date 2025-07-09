package com.mpesa.acquisition.repository;

import com.mpesa.acquisition.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    Optional<TransactionHistory> findByTransactionId(String transactionId);

    List<TransactionHistory> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);

    @Query("SELECT t FROM TransactionHistory t WHERE t.status = :status ORDER BY t.createdAt DESC")
    List<TransactionHistory> findByStatusOrderByCreatedAtDesc(@Param("status") TransactionHistory.TransactionStatus status);

    @Query("SELECT t FROM TransactionHistory t WHERE t.accountNumber = :accountNumber AND t.status = :status")
    List<TransactionHistory> findByAccountNumberAndStatus(@Param("accountNumber") String accountNumber,
                                                         @Param("status") TransactionHistory.TransactionStatus status);

    boolean existsByTransactionId(String transactionId);

    @Query("SELECT COUNT(t) FROM TransactionHistory t WHERE t.status = :status")
    Long countByStatus(@Param("status") TransactionHistory.TransactionStatus status);
}
