package com.example.cloud_service_diploma.service;

import com.example.cloud_service_diploma.exception.SuccessLogout;
import com.example.cloud_service_diploma.model.dto.UserDto;
import com.example.cloud_service_diploma.entity.UserEntity;
import com.example.cloud_service_diploma.exception.BadCredentials;
import com.example.cloud_service_diploma.model.Login;
import com.example.cloud_service_diploma.repositories.UserRepository;
import com.example.cloud_service_diploma.security.JWTToken;
import com.example.cloud_service_diploma.utils.HashUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


@Service
public class AuthorizationService {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    private final UserRepository userRepository;
    private final JWTToken jwtToken;
    //private final PasswordEncoder passwordEncoder;

    public AuthorizationService(UserRepository userRepository, JWTToken jwtToken) {
        this.userRepository = userRepository;
        this.jwtToken = jwtToken;
    }

    public Login authorizationLogin(UserDto userDto) throws NoSuchAlgorithmException {
        log.info("Поиск пользователя в базе данных по логину: {}", userDto.getLogin());
        final UserEntity userFromDatabase = userRepository.findUserByLogin(userDto.getLogin()).orElseThrow(()
                -> new BadCredentials("Пользователь не найден", 400));

        byte[] hashedPassword = HashUtils.hash(userDto.getLogin(), userDto.getPassword());
        if (!Arrays.equals(hashedPassword, userDto.getPassword().getBytes())) {
            final String token = jwtToken.generateToken(userFromDatabase);
            jwtToken.validateToken(token);
            Login login = new Login(token);
            return login;
        } else {
            throw new BadCredentials("Неправильный пароль", 400);
        }
    }

    public String logout(String authToken, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findUserByLogin(auth.getPrincipal().toString()).orElseThrow(()
                -> new BadCredentials("Пользователь не найден", 400));
        log.info("Пользователь начал процедуру выхода из системы: {}", userEntity);

        if (userEntity == null) {
            throw new BadCredentials("Пользователь не найден", 400);
        }

        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, auth);

        jwtToken.removeToken(authToken);
        log.info("Токен пользователя: {} удален из списка активных токенов. Auth-token: {}", userEntity, authToken);
        throw new SuccessLogout("Пользователь " + userEntity.getLogin() + " успешно вышел из системы.", 200);
    }
}