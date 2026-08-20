package com.nazar.usermanagementsystem.service;

import com.nazar.usermanagementsystem.dto.request.LoginRequest;
import com.nazar.usermanagementsystem.dto.request.LogoutRequest;
import com.nazar.usermanagementsystem.dto.request.RefreshTokenRequest;
import com.nazar.usermanagementsystem.dto.response.AuthResponse;
import com.nazar.usermanagementsystem.entity.Role;
import com.nazar.usermanagementsystem.entity.User;
import com.nazar.usermanagementsystem.exception.InvalidCredentialsException;
import com.nazar.usermanagementsystem.exception.InvalidRefreshTokenException;
import com.nazar.usermanagementsystem.exception.UserNotFoundException;
import com.nazar.usermanagementsystem.repository.RevokedTokenRepository;
import com.nazar.usermanagementsystem.repository.UserRepository;
import com.nazar.usermanagementsystem.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 *
 * Notes:
 * - UserService is mocked here rather than exercised directly, since
 *   AuthService.register() simply delegates to it.
 * - Adjust JwtService method names if yours differ from generateToken,
 *   generateRefreshToken, extractUsername, isRefreshToken, isTokenValid.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("john");
        existingUser.setEmail("john@example.com");
        existingUser.setPassword("hashed-password");
        existingUser.setRole(Role.USER);
    }

    @Test
    void login_returnsTokens_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("plain-password");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("plain-password", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken("john")).thenReturn("access-token");
        when(jwtService.generateRefreshToken("john")).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void login_throwsInvalidCredentialsException_whenUserDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("whatever");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_throwsInvalidCredentialsException_whenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("wrong-password");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void refreshToken_returnsNewTokens_whenRefreshTokenIsValid() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existingUser));
        when(jwtService.isRefreshToken("valid-refresh-token")).thenReturn(true);
        when(jwtService.isTokenValid("valid-refresh-token", "john")).thenReturn(true);
        when(jwtService.generateToken("john")).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken("john")).thenReturn("new-refresh-token");

        AuthResponse response = authService.refreshToken(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void refreshToken_throwsInvalidRefreshTokenException_whenTokenIsNotARefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("access-token-used-as-refresh");

        when(jwtService.extractUsername("access-token-used-as-refresh")).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existingUser));
        when(jwtService.isRefreshToken("access-token-used-as-refresh")).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void refreshToken_throwsUserNotFoundException_whenUserNoLongerExists() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("ghost");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void logout_savesRevokedToken_whenTokenNotAlreadyRevoked() {
        LogoutRequest request = new LogoutRequest();
        request.setToken("some-access-token");

        when(revokedTokenRepository.existsByToken("some-access-token")).thenReturn(false);

        authService.logout(request);

        verify(revokedTokenRepository).save(any());
    }

    @Test
    void logout_doesNotSaveAgain_whenTokenAlreadyRevoked() {
        LogoutRequest request = new LogoutRequest();
        request.setToken("already-revoked-token");

        when(revokedTokenRepository.existsByToken("already-revoked-token")).thenReturn(true);

        authService.logout(request);

        verify(revokedTokenRepository, never()).save(any());
    }
}
