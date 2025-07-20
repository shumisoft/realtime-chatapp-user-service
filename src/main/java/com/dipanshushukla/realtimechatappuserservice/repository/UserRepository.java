package com.dipanshushukla.realtimechatappuserservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dipanshushukla.realtimechatappuserservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query("""
                SELECT u FROM User u
                WHERE u.userId <> :currentUserId
                AND (
                    LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
                )
            """)
    Page<User> searchUsers(
            @Param("query") String query,
            @Param("currentUserId") UUID currentUserId,
            Pageable pageable);

}