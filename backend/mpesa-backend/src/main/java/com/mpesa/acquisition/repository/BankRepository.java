package com.mpesa.acquisition.repository;

import com.mpesa.acquisition.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findByValue(String value);

    @Query("SELECT b FROM Bank b ORDER BY b.value ASC")
    List<Bank> findAllOrderByValue();

    boolean existsByValue(String value);
}
