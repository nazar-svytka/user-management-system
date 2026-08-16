package com.nazar.usermanagementsystem.service;

import com.nazar.usermanagementsystem.dto.request.LoginRequest;
import com.nazar.usermanagementsystem.dto.request.LogoutRequest;
import com.nazar.usermanagementsystem.dto.request.RefreshTokenRequest;
import com.nazar.usermanagementsystem.dto.request.UserRequest;
import com.nazar.usermanagementsystem.dto.response.AuthResponse;
import com.nazar.usermanagementsystem.dto.response.UserResponse;
import com.nazar.usermanagementsystem.entity.RevokedToken;
import com.nazar.usermanagementsystem.entity.User;
import com.nazar.usermanagementsystem.exception.InvalidCredentialsException;
import com.nazar.usermanagementsystem.exception.InvalidRefreshTokenException;
import com.nazar.usermanagementsystem.exception.UserNotFoundException;
import com.nazar.usermanagementsystem.repository.RevokedTokenRepository;
import com.nazar.usermanagementsystem.repository.UserRepository;
import com.nazar.usermanagementsystem.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RevokedTokenRepository revokedTokenRepository;

    public AuthService(
            UserRepository userRepository,
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RevokedTokenRepository revokedTokenRepository
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    public UserResponse register(UserRequest request) {
        return userService.createUser(request);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getRole().name()
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (!jwtService.isRefreshToken(refreshToken)
                || !jwtService.isTokenValid(refreshToken, username)) {
            throw new InvalidRefreshTokenException();
        }

        String newAccessToken = jwtService.generateToken(user.getUsername());
        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                user.getRole().name()
        );
    }

    public void logout(LogoutRequest request) {

        if (!revokedTokenRepository.existsByToken(request.getToken())) {
            revokedTokenRepository.save(
                    new RevokedToken(request.getToken())
            );
        }
    }
}