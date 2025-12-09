package com.dipanshushukla.realtimechatappuserservice.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(
            @RequestHeader("X-User-Id") UUID userId) {

        return ResponseEntity.ok(service.getUserById(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<String> updateMyProfile(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody UserDTO userDTO) {

        service.updateUserById(userId, userDTO);
        return ResponseEntity.ok("User updated successfully.");
    }

    @GetMapping("/lookup")
    public ResponseEntity<UserDTO> getUserByUsername(
            @RequestParam String username) {
        return ResponseEntity.ok(service.getUserByUsername(username));
    }
}
