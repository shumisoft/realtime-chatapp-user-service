package com.dipanshushukla.realtimechatappuserservice.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.dto.UserSearchResponseDTO;
import com.dipanshushukla.realtimechatappuserservice.dto.UsernameExistsResponseDTO;
import com.dipanshushukla.realtimechatappuserservice.entity.User;
import com.dipanshushukla.realtimechatappuserservice.exception.InvalidUserUpdateException;
import com.dipanshushukla.realtimechatappuserservice.exception.UserNotFoundException;
import com.dipanshushukla.realtimechatappuserservice.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserDTO getUserById(UUID userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user found with id: " + userId));
        return UserDTO.fromEntity(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user found with username: " + username));
        return UserDTO.fromEntity(user);
    }

    public UserDTO updateUserById(UUID userId, UserDTO userDTO) {

        if (userDTO.getEmail() == null &&
                userDTO.getFullName() == null &&
                userDTO.getAvatar() == null &&
                userDTO.getBio() == null) {

            throw new InvalidUserUpdateException(
                    "Provide at least one: email, fullName, avatar, bio.");
        }

        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user found with id: " + userId));

        if (userDTO.getEmail() != null)
            user.setEmail(userDTO.getEmail());
        if (userDTO.getFullName() != null)
            user.setFullName(userDTO.getFullName());
        if (userDTO.getAvatar() != null)
            user.setAvatar(userDTO.getAvatar());
        if (userDTO.getBio() != null)
            user.setBio(userDTO.getBio());

        return UserDTO.fromEntity(repository.save(user));
    }

    public void deleteById(UUID userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user found with id: " + userId));
        repository.delete(user);
    }

    // TODO Move to auth later
    public UsernameExistsResponseDTO existsByUsername(String username) {
        return UsernameExistsResponseDTO.builder().exists(repository.existsByUsername(username)).build();
    }

    public Page<UserSearchResponseDTO> searchUsers(
            String query,
            UUID currentUserId,
            int page,
            int size) {

        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query must not be empty");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<User> users = repository.searchUsers(query.trim(), currentUserId, pageable);

        return users.map(UserSearchResponseDTO::fromEntity);
    }

}
