package com.example.wallet.repository;

import com.example.wallet.enums.TransactionStatus;
import com.example.wallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Modifying // which consider the query is modifying query
    @Query("update Transaction t set t.transactionStatus = ?2 where t.transactionId = ?1")
    void updateTransaction(String transactionId, TransactionStatus transactionStatus);
}
