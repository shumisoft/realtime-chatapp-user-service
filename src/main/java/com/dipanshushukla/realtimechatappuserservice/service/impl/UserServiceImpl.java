package com.dipanshushukla.realtimechatappuserservice.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.dto.UserSearchResponseDTO;
import com.dipanshushukla.realtimechatappuserservice.entity.User;
import com.dipanshushukla.realtimechatappuserservice.exception.InvalidUserUpdateException;
import com.dipanshushukla.realtimechatappuserservice.exception.UserNotFoundException;
import com.dipanshushukla.realtimechatappuserservice.repository.UserRepository;
import com.dipanshushukla.realtimechatappuserservice.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    private static final String USER_NOT_FOUND_WITH_ID_MESSAGE = "No user found with id: ";

    @Override
    public UserDTO getUserById(UUID userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + userId));
        return UserDTO.fromEntity(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user found with username: " + username));
        return UserDTO.fromEntity(user);
    }

    @Override
    public UserDTO updateUserById(UUID userId, UserDTO userDTO) {

        if (userDTO.getEmail() == null &&
                userDTO.getFullName() == null &&
                userDTO.getAvatar() == null &&
                userDTO.getBio() == null) {

            throw new InvalidUserUpdateException(
                    "Provide at least one: email, fullName, avatar, bio.");
        }

        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + userId));

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

    @Override
    public void deleteById(UUID userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + userId));
        repository.delete(user);
    }

    @Override
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
