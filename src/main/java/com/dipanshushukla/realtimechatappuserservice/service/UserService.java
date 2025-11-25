package com.dipanshushukla.realtimechatappuserservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.entity.User;
import com.dipanshushukla.realtimechatappuserservice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserDTO getUserById(UUID userId) throws EntityNotFoundException {
        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No user found with id: " + userId));
        return UserDTO.fromEntity(user);
    }

    public UserDTO getUserByUsername(String username) throws EntityNotFoundException {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("No user found with username: " + username));
        return UserDTO.fromEntity(user);
    }

    public void updateUserById(UUID userId, UserDTO userDTO)
            throws EntityNotFoundException, IllegalArgumentException {

        if (userDTO.getEmail() == null &&
                userDTO.getFullName() == null &&
                userDTO.getAvatar() == null &&
                userDTO.getBio() == null) {

            throw new IllegalArgumentException("Provide at least one: email, fullName, avatar, bio.");
        }

        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No user found with id: " + userId));

        if (userDTO.getEmail() != null)
            user.setEmail(userDTO.getEmail());
        if (userDTO.getFullName() != null)
            user.setFullName(userDTO.getFullName());
        if (userDTO.getAvatar() != null)
            user.setAvatar(userDTO.getAvatar());
        if (userDTO.getBio() != null)
            user.setBio(userDTO.getBio());

        repository.save(user);
    }

    public void deleteById(UUID userId) throws EntityNotFoundException {
        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No user found with id: " + userId));
        repository.delete(user);
    }
}
