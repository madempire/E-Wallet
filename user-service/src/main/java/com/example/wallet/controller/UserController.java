package com.example.wallet.controller;

import com.example.wallet.model.User;
import com.example.wallet.requestDto.UserCreateRequest;
import com.example.wallet.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;
// User:
//    user creation
//    getting user // based on login he can get his details
// Admin:
//    admin/getAllUsers
//    admin/user/userId
//    update user
//    delete user

    @PostMapping("/user") //access able by everyone
    public ResponseEntity createUsers(@RequestBody UserCreateRequest userCreateRequest) throws JsonProcessingException {
        return new ResponseEntity(userService.create(userCreateRequest), HttpStatus.OK);
    }

    @GetMapping("/user") //loged in users
    public ResponseEntity getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return new ResponseEntity(userService.loadUserByUsername(user.getUsername()), HttpStatus.OK);
    }

    @GetMapping("/admin/all/users") //admin
    public ResponseEntity getAllUser(){
        return new ResponseEntity(userService.getAllUserDetails(),HttpStatus.OK);
    }

    @GetMapping("/admin/user/{userId}") // admin
    public ResponseEntity getUserById(@PathVariable("userId") String userId){
        return new ResponseEntity(userService.loadUserByUsername(userId),HttpStatus.OK);
    }
}
