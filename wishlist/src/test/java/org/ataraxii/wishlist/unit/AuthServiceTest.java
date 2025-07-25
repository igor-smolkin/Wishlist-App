package org.ataraxii.wishlist.unit;

import org.ataraxii.wishlist.database.entity.User;
import org.ataraxii.wishlist.database.repository.SessionRepository;
import org.ataraxii.wishlist.database.repository.UserRepository;
import org.ataraxii.wishlist.dto.authentication.AuthRequestDto;
import org.ataraxii.wishlist.dto.authentication.AuthResponseDto;
import org.ataraxii.wishlist.exception.ConflictException;
import org.ataraxii.wishlist.exception.NotFoundException;
import org.ataraxii.wishlist.service.AuthService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Nested
    class RegisterTests {

        @Test
        void registerUser_valid_saveUser() {
            AuthRequestDto request = new AuthRequestDto();

            request.setUsername("testuser");
            request.setPassword("testpassword");

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

            AuthResponseDto response = authService.registerUser(request);

            verify(userRepository).save(any());

            assertNotNull(response);
            assertNotNull(response.getUser());
            assertEquals("testuser", response.getUser().getUsername());
        }

        @Test
        void registerUser_alreadyExist_throwException() {
            AuthRequestDto request = new AuthRequestDto();

            request.setUsername("testuser");
            request.setPassword("testpassword");

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);
            assertThrows(ConflictException.class, () -> authService.registerUser(request));
        }
    }

    @Nested
    class LoginTests {

        @Test
        void loginUser_valid_openSession() {
            AuthRequestDto request = new AuthRequestDto();

            request.setUsername("testuser");
            request.setPassword("testpassword");

            User mockUser = User.builder()
                    .id(UUID.fromString("ea7c7381-a19c-49a9-92cd-d32c3db25092"))
                    .username("testuser")
                    .password("hashedpassword")
                    .build();

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches("testpassword", "hashedpassword")).thenReturn(true);

            AuthResponseDto response = authService.loginUser(request);

            assertNotNull(response);
            assertNotNull(response.getUser());
            assertNotNull(response.getSession());
            assertEquals("testuser", response.getUser().getUsername());
        }

        @Test
        void loginUser_userNotFound_throwException() {
            AuthRequestDto request = new AuthRequestDto();

            request.setUsername("testuser");
            request.setPassword("testpassword");

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> authService.loginUser(request));
        }
    }
}
