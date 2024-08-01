package com.project.springboot.e_bank.repository;

import com.project.springboot.e_bank.entity.Transactions;
import jakarta.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findByAccountIdOrderByTimestampDesc(Long accountId);
}
