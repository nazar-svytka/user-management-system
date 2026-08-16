package com.nazar.usermanagementsystem.repository;

import com.nazar.usermanagementsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username,
            String email,
            Pageable pageable
    );

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

}