package com.dipanshushukla.realtimechatappuserservice.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.dto.UserSearchResponseDTO;
import com.dipanshushukla.realtimechatappuserservice.dto.UsernameExistsResponseDTO;
import com.dipanshushukla.realtimechatappuserservice.service.UserService;

import jakarta.validation.constraints.NotBlank;

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
    public ResponseEntity<UserDTO> updateMyProfile(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody UserDTO userDTO) {

        return ResponseEntity.ok(service.updateUserById(userId, userDTO));
    }

    @GetMapping("/lookup")
    public ResponseEntity<UserDTO> getUserByUsername(
            @RequestParam @NotBlank(message = "Username must not be empty or null") String username) {
        return ResponseEntity.ok(service.getUserByUsername(username));
    }

    // TODO Move to auth
    @GetMapping("/exists")
    public ResponseEntity<UsernameExistsResponseDTO> existByUsername(
            @RequestParam @NotBlank(message = "Username must not be empty or null") String username) {
        return ResponseEntity.ok(service.existsByUsername(username));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserSearchResponseDTO>> searchUsers(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.searchUsers(q, currentUserId, page, size));

    }

}
