package com.dipanshushukla.realtimechatappuserservice.dto;

import java.util.UUID;

import com.dipanshushukla.realtimechatappuserservice.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSearchResponseDTO {

  private UUID userId;
  private String username;
  private String fullName;
  private String avatar;

  public static UserSearchResponseDTO fromEntity(User entity) {

    return UserSearchResponseDTO.builder()
        .userId(entity.getUserId())
        .username(entity.getUsername())
        .fullName(entity.getFullName())
        .avatar(entity.getAvatar())
        .build();

  }

}
