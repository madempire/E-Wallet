package com.example.wallet.service;

import com.example.wallet.model.User;
import com.example.wallet.requestDto.UserCreateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface UserService {
    User create(UserCreateRequest userCreateRequest) throws JsonProcessingException;
    List<User> getAllUserDetails();

    User loadUserByUsername(String phoneNumber);
}
