package com.dipanshushukla.realtimechatappuserservice.factory;

import java.util.UUID;

import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.dto.UserSearchResponseDTO;
import com.dipanshushukla.realtimechatappuserservice.entity.User;

public class UserDataFactory {

	public static final UUID DEFAULT_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
	public static final String DEFAULT_USERNAME = "test_user";
	public static final String DEFAULT_EMAIL = "test@example.com";
	public static final String DEFAULT_FULL_NAME = "Test User";
	public static final String DEFAULT_BIO = "This is a dummy bio";

	public static User createVlidUser() {

		return User.builder()
				.userId(DEFAULT_USER_ID)
				.username(DEFAULT_USERNAME)
				.email(DEFAULT_EMAIL)
				.fullName(DEFAULT_FULL_NAME)
				.bio(DEFAULT_BIO)
				.build();
	}

	public static UserDTO createValidUserDTO() {

		return UserDTO.builder()
				.userId(DEFAULT_USER_ID)
				.username(DEFAULT_USERNAME)
				.email(DEFAULT_EMAIL)
				.fullName(DEFAULT_FULL_NAME)
				.bio(DEFAULT_BIO).build();

	}

	public static UserSearchResponseDTO createValidSearchResponseDTO() {

		return UserSearchResponseDTO.builder()
				.userId(DEFAULT_USER_ID)
				.username(DEFAULT_USERNAME)
				.fullName(DEFAULT_FULL_NAME)
				.build();

	}
}
