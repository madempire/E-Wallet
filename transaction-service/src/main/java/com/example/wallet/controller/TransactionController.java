package com.example.wallet.controller;

import com.example.wallet.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @PostMapping("/transact")
    public String initiateTransaction(@RequestParam("receiver") String receiver,
                                      @RequestParam("message") String message,
                                      @RequestParam("amount") Double amount) throws JsonProcessingException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return transactionService.initiateTransaction(userDetails.getUsername(), receiver, message, amount);
    }
}
