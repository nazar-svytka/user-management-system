package com.nazar.usermanagementsystem.controller;

import com.nazar.usermanagementsystem.dto.request.LoginRequest;
import com.nazar.usermanagementsystem.dto.request.LogoutRequest;
import com.nazar.usermanagementsystem.dto.request.RefreshTokenRequest;
import com.nazar.usermanagementsystem.dto.request.UserRequest;
import com.nazar.usermanagementsystem.dto.response.AuthResponse;
import com.nazar.usermanagementsystem.dto.response.UserResponse;
import com.nazar.usermanagementsystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Authentication",
        description = "Authentication API"
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account"
    )
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody UserRequest request) {
        return authService.register(request);
    }

    @Operation(
            summary = "Login",
            description = "Authenticates user and returns access and refresh tokens"
    )
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token"
    )
    @PostMapping("/refresh")
    public AuthResponse refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return authService.refreshToken(request);
    }

    @Operation(
            summary = "Logout",
            description = "Revokes JWT token"
    )
    @PostMapping("/logout")
    public String logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        authService.logout(request);
        return "Logged out successfully";
    }
}