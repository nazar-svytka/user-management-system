package com.nazar.usermanagementsystem.mapper;

import com.nazar.usermanagementsystem.dto.request.UserRequest;
import com.nazar.usermanagementsystem.dto.response.UserResponse;
import com.nazar.usermanagementsystem.entity.User;

public class UserMapper {

    public static User toEntity(UserRequest request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        return user;
    }

    public static UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}