package com.nazar.usermanagementsystem.service;

import com.nazar.usermanagementsystem.dto.request.UpdateUserRequest;
import com.nazar.usermanagementsystem.dto.request.UserRequest;
import com.nazar.usermanagementsystem.dto.response.UserResponse;
import com.nazar.usermanagementsystem.entity.Role;
import com.nazar.usermanagementsystem.entity.User;
import com.nazar.usermanagementsystem.exception.UserNotFoundException;
import com.nazar.usermanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 *
 * Notes:
 * - UserMapper.toResponse / toEntity are static helpers; these tests assume
 *   they perform a straightforward field-by-field mapping (id, username,
 *   email, role). If your mapper differs, adjust the assertions below.
 * - Adjust getter names on UpdateUserRequest / UserRequest if they differ
 *   from what is used here (getUsername, getEmail, getPassword, getRole).
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
    void getUserById_returnsUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserResponse response = userService.getUserById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_throwsUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(999L);
    }

    @Test
    void getAllUsers_returnsPagedResults_withoutSearch() {
        Page<User> page = new PageImpl<>(List.of(existingUser));

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<UserResponse> result = userService.getAllUsers(0, 5, null, "username,asc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("john");
        verify(userRepository).findAll(any(Pageable.class));
        verify(userRepository, never())
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        any(), any(), any());
    }

    @Test
    void getAllUsers_usesSearch_whenSearchTermProvided() {
        Page<User> page = new PageImpl<>(List.of(existingUser));

        when(userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        eq("john"), eq("john"), any(Pageable.class)))
                .thenReturn(page);

        Page<UserResponse> result = userService.getAllUsers(0, 5, "john", "username,asc");

        assertThat(result.getContent()).hasSize(1);
        verify(userRepository)
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        eq("john"), eq("john"), any(Pageable.class));
        verify(userRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void createUser_encodesPasswordAndSavesUser() {
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("plain-password");

        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(42L);
            if (u.getRole() == null) {
                u.setRole(Role.USER);
            }
            return u;
        });

        UserResponse response = userService.createUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getEmail()).isEqualTo("newuser@example.com");

        verify(passwordEncoder).encode("plain-password");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void updateUser_updatesFields_whenUserExists() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("john-updated");
        request.setEmail("john-updated@example.com");
        request.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userService.updateUser(1L, request);

        assertThat(response.getUsername()).isEqualTo("john-updated");
        assertThat(response.getEmail()).isEqualTo("john-updated@example.com");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void updateUser_throwsUserNotFoundException_whenUserDoesNotExist() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("ghost");
        request.setEmail("ghost@example.com");
        request.setRole(Role.USER);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_deletesUser_whenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_throwsUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).deleteById(anyLong());
    }
}
