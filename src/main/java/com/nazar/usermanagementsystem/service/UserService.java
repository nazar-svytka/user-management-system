package com.nazar.usermanagementsystem.service;

import com.nazar.usermanagementsystem.dto.request.UpdateUserRequest;
import com.nazar.usermanagementsystem.dto.request.UserRequest;
import com.nazar.usermanagementsystem.dto.response.UserResponse;
import com.nazar.usermanagementsystem.entity.User;
import com.nazar.usermanagementsystem.exception.UserAlreadyExistsException;
import com.nazar.usermanagementsystem.exception.UserNotFoundException;
import com.nazar.usermanagementsystem.mapper.UserMapper;
import com.nazar.usermanagementsystem.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponse> getAllUsers(
            int page,
            int size,
            String search,
            String sort
    ) {

        Sort sorting;

        if (sort.contains(",")) {

            String[] parts = sort.split(",");

            sorting = parts[1].equalsIgnoreCase("desc")
                    ? Sort.by(parts[0]).descending()
                    : Sort.by(parts[0]).ascending();

        } else {

            sorting = Sort.by(sort).ascending();

        }

        Pageable pageable = PageRequest.of(page, size, sorting);

        if (search == null || search.isBlank()) {

            return userRepository.findAll(pageable)
                    .map(UserMapper::toResponse);

        }

        return userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        search,
                        search,
                        pageable
                )
                .map(UserMapper::toResponse);
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toResponse(user);
    }

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        User user = UserMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return UserMapper.toResponse(
                userRepository.save(user)
        );
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        return UserMapper.toResponse(
                userRepository.save(user)
        );
    }

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }

}