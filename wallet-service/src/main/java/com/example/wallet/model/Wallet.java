package com.example.wallet.model;


import com.example.wallet.UserIdentifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Long userId;

    private String phoneNumber;

    private Double balance;

    @Enumerated(EnumType.STRING)
    private UserIdentifier userIdentifier;

    private String identifierValue;
}
// as soon as create user to create a wallet api is not needed
