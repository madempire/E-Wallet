package com.example.wallet.repository;

import com.example.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    Wallet findByPhoneNumber(String sender);

    //to deduct pass -ve value and for credit pass +ve balance this query perform both tasks

    @Modifying
    @Query("update Wallet w set w.balance = w.balance+ ?1 where w.phoneNumber = ?2")
    void updateWallet(Double amount, String phoneNumber);

}
