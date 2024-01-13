package com.example.wallet;

import com.example.wallet.constants.UserConstants;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User txnServiceUser = User.builder().phoneNumber("txnService")
                .password(passwordEncoder.encode("txn123"))
                .authorities(UserConstants.SERVICE_AUTHORITY)
                .userIdentifier(UserIdentifier.SERVICE_ID)
                .identifierValue("txn123")
                .email("txn@gmail.com").build();
        userRepository.save(txnServiceUser);
    }
}
