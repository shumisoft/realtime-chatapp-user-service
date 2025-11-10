package com.dipanshushukla.realtimechatappuserservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.dto.UserSearchResponseDTO;
import com.dipanshushukla.realtimechatappuserservice.exception.UserNotFoundException;
import com.dipanshushukla.realtimechatappuserservice.exception.handler.GlobalExceptionHandler;
import com.dipanshushukla.realtimechatappuserservice.factory.UserDataFactory;
import com.dipanshushukla.realtimechatappuserservice.service.UserService;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private UserDTO mockUserDTO;

	@BeforeEach
	void setUp() {

		// Wire the controller AND the GlobalExceptionHandler
		mockMvc = MockMvcBuilders
				.standaloneSetup(userController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();

		mockUserDTO = UserDataFactory.createValidUserDTO();

	}

	@Test
	@DisplayName("GET /me - Should return 200 OK and user profile")
	void getMyProfile_success() throws Exception {

		when(userService.getUserById(UserDataFactory.DEFAULT_USER_ID)).thenReturn(mockUserDTO);

		mockMvc.perform(get("/me")
				.header("X-User-Id", UserDataFactory.DEFAULT_USER_ID.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(UserDataFactory.DEFAULT_USERNAME))
				.andExpect(jsonPath("$.email").value(UserDataFactory.DEFAULT_EMAIL));

	}

	@Test
	@DisplayName("GET /me - Should return 400 Bad Request if X-User-Id header is missing")
	void getMyProfile_MissingHeader() throws Exception {

		mockMvc.perform(get("/me"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(userService);

	}

	@Test
	@DisplayName("PATCH /me - Should return 200 OK on successful update")
	void updateMyProfile_Success() throws Exception {

		UserDTO updateRequest = new UserDTO();
		updateRequest.setBio("New Bio");

		when(userService.updateUserById(eq(UserDataFactory.DEFAULT_USER_ID), any(UserDTO.class)))
				.thenReturn(mockUserDTO);

		mockMvc.perform(patch("/me")
				.header("X-User-Id", UserDataFactory.DEFAULT_USER_ID.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(UserDataFactory.DEFAULT_USERNAME));

	}

	@Test
	@DisplayName("GET /lookup - Should return 200 OK when username exists")
	void getUserByUsername_Success() throws Exception {

		when(userService.getUserByUsername(UserDataFactory.DEFAULT_USERNAME)).thenReturn(mockUserDTO);

		mockMvc.perform(get("/lookup")
				.param("username", UserDataFactory.DEFAULT_USERNAME))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(UserDataFactory.DEFAULT_USERNAME));
	}

	@Test
	@DisplayName("GET /lookup - Should return 404 Not Found if user doesn't exist")
	void getUserByUsername_NotFound() throws Exception {

		when(userService.getUserByUsername("unknown_user"))
				.thenThrow(new UserNotFoundException("No user found with username: unknown_user"));

		mockMvc.perform(get("/lookup")
				.param("username", "unknown_user"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No user found with username: unknown_user"));

	}

	@Test
	@DisplayName("GET /lookup - Should return 400 Bad Request if username param is blank")
	void getUserByUsername_BlankParam() throws Exception {
		mockMvc.perform(get("/lookup")
				.param("username", "   "))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Username must not be empty or null"));

		verifyNoInteractions(userService);
	}

	@Test
	@DisplayName("GET /search - Should return 200 OK and paginated results")
	void searchUsers_Success() throws Exception {
		UserSearchResponseDTO responseDTO = UserDataFactory.createValidSearchResponseDTO();

		Page<UserSearchResponseDTO> mockPage = new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 10), 1);

		when(userService.searchUsers(anyString(), eq(UserDataFactory.DEFAULT_USER_ID), anyInt(), anyInt()))
				.thenReturn(mockPage);

		mockMvc.perform(get("/search")
				.header("X-User-Id", UserDataFactory.DEFAULT_USER_ID.toString())
				.param("q", "test")
				.param("page", "0")
				.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].username").value(UserDataFactory.DEFAULT_USERNAME))
				.andExpect(jsonPath("$.totalElements").value(1));
	}

}
