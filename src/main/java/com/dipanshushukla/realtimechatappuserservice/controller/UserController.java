package com.dipanshushukla.realtimechatappuserservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.service.UserService;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID userId) {
        UserDTO userDTO = service.getUserById(userId);
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUserById(@PathVariable UUID userId,
            @RequestBody UserDTO userDTO) {
        service.updateUserById(userId, userDTO);
        return ResponseEntity.ok("User updated successfully.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteById(@PathVariable UUID userId) {
        service.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }

    @GetMapping()
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam String username) {
        UserDTO userDTO = service.getUserByUsername(username);
        return ResponseEntity.ok(userDTO);
    }
}
