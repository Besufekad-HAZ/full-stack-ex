package com.mpesa.acquisition.repository;

import com.mpesa.acquisition.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByBankIdOrderByValue(Long bankId);

    Optional<Branch> findByValueAndBankId(String value, Long bankId);

    @Query("SELECT b FROM Branch b WHERE b.bankId = :bankId ORDER BY b.value ASC")
    List<Branch> findBranchesByBankIdOrderByValue(@Param("bankId") Long bankId);

    boolean existsByValueAndBankId(String value, Long bankId);
}
