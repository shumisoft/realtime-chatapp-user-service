package com.dipanshushukla.realtimechatappuserservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.dipanshushukla.realtimechatappuserservice.entity.User;

@DataJpaTest
class UserRepositoryTest {

        @Autowired
        private UserRepository userRepository;

        private UUID currentUserId;

        @BeforeEach
        void setUp() {
                User currentUser = User.builder()
                                .username("searcher_man")
                                .email("searcher@example.com")
                                .fullName("Searcher Guy")
                                .build();

                User targetUser1 = User.builder()
                                .username("test_sde")
                                .email("sde@example.com")
                                .fullName("Software Engineer")
                                .build();

                User targetUser2 = User.builder()
                                .username("john_doe")
                                .email("john@example.com")
                                .fullName("John Test")
                                .build();

                User unrelatedUser = User.builder()
                                .username("random_guy")
                                .email("random@example.com")
                                .fullName("Bob Builder")
                                .build();

                // Save and capture the returned entity to get the generated ID
                currentUser = userRepository.save(currentUser);
                currentUserId = currentUser.getUserId();

                userRepository.saveAll(List.of(targetUser1, targetUser2, unrelatedUser));
        }

        @Test
        @DisplayName("Should find users matching partial username OR fullName (Case Insensitive)")
        void searchUsers_ShouldReturnMatchingUsers() {
                // Act: Search for "test" (Should match 'test_sde' and 'John Test')
                Page<User> result = userRepository.searchUsers("test", currentUserId, PageRequest.of(0, 10));

                // Assert
                List<String> usernames = result.getContent()
                                .stream()
                                .map(User::getUsername)
                                .toList();

                assertTrue(usernames.containsAll(List.of("test_sde", "john_doe"))
                                && usernames.size() == 2);
        }

        @Test
        @DisplayName("Should NOT return the current user even if they match the query")
        void searchUsers_ShouldExcludeCurrentUser() {
                // Act: Search for "search" (Matches the current user's username/name)
                Page<User> result = userRepository.searchUsers("search", currentUserId, PageRequest.of(0, 10));

                // Assert: They should be completely excluded from the results
                assertEquals(0, result.getTotalElements());
                assertTrue(result.getContent().isEmpty());
        }

        @Test
        @DisplayName("Should return empty page if no users match")
        void searchUsers_ShouldReturnEmpty_WhenNoMatch() {
                // Act
                Page<User> result = userRepository.searchUsers("xyzabc", currentUserId, PageRequest.of(0, 10));

                // Assert
                assertEquals(0, result.getTotalElements());
        }
}