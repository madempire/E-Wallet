package com.example.wallet.requestDto;


import com.example.wallet.UserIdentifier;
import com.example.wallet.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String phoneNumber;// consider this as a userId
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String dob;
    private String country;
    @NotBlank
    private UserIdentifier userIdentifier;
    @NotBlank
    private String identifierValue;

    public User toUser(){
        return User.builder().name(this.name).phoneNumber(this.phoneNumber).email(this.email)
                .password(this.password).dob(this.dob).country(this.country)
                .userIdentifier(this.userIdentifier).identifierValue(this.identifierValue).build();
    }
}
