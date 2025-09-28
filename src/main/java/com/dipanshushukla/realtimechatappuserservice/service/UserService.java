package com.dipanshushukla.realtimechatappuserservice.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.dto.UserSearchResponseDTO;

public interface UserService {

  UserDTO getUserById(UUID userId);

  UserDTO getUserByUsername(String username);

  UserDTO updateUserById(UUID userId, UserDTO userDTO);

  void deleteById(UUID userId);

  Page<UserSearchResponseDTO> searchUsers(
      String query,
      UUID currentUserId,
      int page,
      int size);

}