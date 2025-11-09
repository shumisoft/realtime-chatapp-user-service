package com.dipanshushukla.realtimechatappuserservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.data.domain.Pageable;

import com.dipanshushukla.realtimechatappuserservice.dto.UserDTO;
import com.dipanshushukla.realtimechatappuserservice.dto.UserSearchResponseDTO;
import com.dipanshushukla.realtimechatappuserservice.entity.User;
import com.dipanshushukla.realtimechatappuserservice.exception.InvalidUserUpdateException;
import com.dipanshushukla.realtimechatappuserservice.exception.UserNotFoundException;
import com.dipanshushukla.realtimechatappuserservice.factory.UserDataFactory;
import com.dipanshushukla.realtimechatappuserservice.repository.UserRepository;
import com.dipanshushukla.realtimechatappuserservice.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UserServiceImpl service;

  private User mockUser;

  @BeforeEach
  void setUp() {
    mockUser = UserDataFactory.createVlidUser();
  }

  @Test
  @DisplayName("Should return UserDTO when ID is found")
  void getUserById_Success() {

    // Arrange
    when(repository.findById(UserDataFactory.DEFAULT_USER_ID)).thenReturn(Optional.of(mockUser));

    // Act
    UserDTO result = service.getUserById(UserDataFactory.DEFAULT_USER_ID);

    // Assert
    assertEquals(result.getUsername(), UserDataFactory.DEFAULT_USERNAME);
    assertEquals(result.getEmail(), UserDataFactory.DEFAULT_EMAIL);

  }

  @Test
  @DisplayName("Should throw UserNotFoundException when ID is missing")
  void getUserById_NotFound() {

    when(repository.findById(UserDataFactory.DEFAULT_USER_ID)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> service.getUserById(UserDataFactory.DEFAULT_USER_ID));

  }

  @Test
  @DisplayName("Should return UserDTO when username is found")
  void getUserByUsername_Success() {

    when(repository.findByUsername(UserDataFactory.DEFAULT_USERNAME)).thenReturn(Optional.of(mockUser));

    UserDTO result = service.getUserByUsername(UserDataFactory.DEFAULT_USERNAME);

    assertEquals(result.getUserId(), UserDataFactory.DEFAULT_USER_ID);

  }

  @Test
  @DisplayName("Should throw UserNotFoundException when username is missing")
  void getUserByUsername_NotFound() {

    when(repository.findByUsername(UserDataFactory.DEFAULT_USERNAME)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> service.getUserByUsername(UserDataFactory.DEFAULT_USERNAME));

  }

  @Test
  @DisplayName("Should update ONLY provided fields and preserve existing ones")
  void updateUserById_PartialUpdate_Success() {
    final String UPDATED_BIO = "Updated bio";
    final String UPDATED_FULL_NAME = "Updated Name";

    // Arrange
    UserDTO updatePayload = new UserDTO();
    updatePayload.setBio(UPDATED_BIO);
    updatePayload.setFullName(UPDATED_FULL_NAME);
    // Notice email and avatar are null in the payload

    when(repository.findById(UserDataFactory.DEFAULT_USER_ID)).thenReturn(Optional.of(mockUser));

    // Mock the save to just return whatever entity was passed into it
    when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UserDTO result = service.updateUserById(UserDataFactory.DEFAULT_USER_ID, updatePayload);

    // Assert
    assertEquals(result.getBio(), UPDATED_BIO);
    assertEquals(result.getFullName(), UPDATED_FULL_NAME);
    assertEquals(result.getEmail(), UserDataFactory.DEFAULT_EMAIL);
    assertEquals(result.getUsername(), UserDataFactory.DEFAULT_USERNAME);

    verify(repository).save(any(User.class));

  }

  @Test
  @DisplayName("Should throw InvalidUserUpdateException when payload is completely empty")
  void updateUserById_EmptyPayload_ThrowsException() {

    UserDTO emptyPayload = new UserDTO(); // All fields null

    assertThrows(InvalidUserUpdateException.class,
        () -> service.updateUserById(UserDataFactory.DEFAULT_USER_ID, emptyPayload));

    // Ensure we didn't waste a database call
    verify(repository, never()).findById(any());
    verify(repository, never()).save(any());

  }

  @Test
  @DisplayName("Should delete user when ID exists")
  void deleteById_Success() {

    when(repository.findById(UserDataFactory.DEFAULT_USER_ID)).thenReturn(Optional.of(mockUser));

    service.deleteById(UserDataFactory.DEFAULT_USER_ID);

    verify(repository).delete(mockUser);

  }

  @Test
  @DisplayName("Should map Page<User> to Page<UserSearchResponseDTO> correctly")
  void searchUsers_Success() {

    // Arrange
    UUID currentUserId = UUID.randomUUID();
    Page<User> mockPage = new PageImpl<>(List.of(mockUser));

    when(repository.searchUsers(eq("test"), eq(currentUserId), any(Pageable.class)))
        .thenReturn(mockPage);

    // Act
    Page<UserSearchResponseDTO> result = service.searchUsers("test", currentUserId, 0, 10);

    // Assert
    assertEquals(result.getTotalElements(), 1);
    assertEquals(result.getContent().get(0).getUsername(), UserDataFactory.DEFAULT_USERNAME);

    // Verify page request was constructed correctly
    verify(repository).searchUsers(eq("test"), eq(currentUserId), eq(PageRequest.of(0, 10)));

  }

  @Test
  @DisplayName("Should throw IllegalArgumentException on empty query")
  void searchUsers_EmptyQuery_ThrowsException() {

    assertThrows(IllegalArgumentException.class,
        () -> service.searchUsers("   ", UUID.randomUUID(), 0, 10));

    verify(repository, never()).searchUsers(anyString(), any(), any());

  }

}
