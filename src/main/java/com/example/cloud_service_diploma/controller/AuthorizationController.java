package com.example.cloud_service_diploma.controller;


import com.example.cloud_service_diploma.config.AuthenticationConfigConstants;
import com.example.cloud_service_diploma.model.dto.UserDto;
import com.example.cloud_service_diploma.model.Login;
import com.example.cloud_service_diploma.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;



@RestController
public class AuthorizationController {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationController.class);

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping("/login")
    public ResponseEntity<Login> authorizationLogin(@RequestBody UserDto userDto) throws NoSuchAlgorithmException {
        log.info("Пользователь пытается войти в систему: {}", userDto);
        Login login = authorizationService.authorizationLogin(userDto);
        if (login == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("Пользователь: {} успешно вошел в систему. Auth-token: {}", userDto, login.getToken());
        return new ResponseEntity<>(login, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = AuthenticationConfigConstants.AUTH_TOKEN) String authToken,
                                       HttpServletRequest request, HttpServletResponse response) {
        String userLogout = authorizationService.logout(authToken, request, response);
        if (userLogout == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("Пользователь: {} успешно вышел из системы. Auth-token: {}", userLogout, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
