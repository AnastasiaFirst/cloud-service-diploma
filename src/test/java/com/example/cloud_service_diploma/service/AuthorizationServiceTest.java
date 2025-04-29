package com.example.cloud_service_diploma.service;

import com.example.cloud_service_diploma.entity.UserEntity;
import com.example.cloud_service_diploma.exception.SuccessLogout;
import com.example.cloud_service_diploma.model.dto.UserDto;
import com.example.cloud_service_diploma.repositories.UserRepository;
import com.example.cloud_service_diploma.security.JWTToken;
import com.example.cloud_service_diploma.utils.HashUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTToken jwtToken;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthorizationLoginSuccess() throws NoSuchAlgorithmException {

        UserDto userDto = new UserDto("testUser ", "password");
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin("testUser ");
        userEntity.setPassword("hashedPassword");

        when(userRepository.findUserByLogin(userDto.getLogin())).thenReturn(Optional.of(userEntity));
        when(jwtToken.generateToken(userEntity)).thenReturn("mockToken");
        when(HashUtils.hash(userDto.getLogin(), userDto.getPassword())).thenReturn("hashedPassword".getBytes());

        assertNotNull(authorizationService.authorizationLogin(userDto));
        assertEquals("mockToken", authorizationService.authorizationLogin(userDto).getToken());
        verify(jwtToken).generateToken(userEntity);
    }

    @Test
    void testLogoutSuccess() {

        String authToken = "mockToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin("testUser ");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("testUser ");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findUserByLogin("testUser ")).thenReturn(Optional.of(userEntity));

        assertThrows(SuccessLogout.class, () -> {
            authorizationService.logout(authToken, request, response);
        });

        verify(jwtToken).removeToken(authToken);
        verify(userRepository).findUserByLogin("testUser ");
    }

}