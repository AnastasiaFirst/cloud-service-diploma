package com.example.cloud_service_diploma.security;

import com.example.cloud_service_diploma.config.AuthenticationConfigConstants;
import com.example.cloud_service_diploma.entity.UserEntity;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JWTToken {
    private static final Logger log = LoggerFactory.getLogger(JWTToken.class);
    private final SecretKey secretKey;
    private UserEntity userEntity;
    private final Set<String> activeTokens = ConcurrentHashMap.newKeySet();


    public JWTToken(@Value("${jwt.secretKey}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public UserEntity getAuthenticatedUser() {
        return userEntity;
    }

    public String generateToken(@NonNull UserEntity userEntity) throws IllegalArgumentException {
        this.userEntity = userEntity;
        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + 3600000);

        String token = Jwts.builder()
                .setId(String.valueOf(userEntity.getId()))
                .setSubject(userEntity.getLogin())
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(exp)
                .signWith(secretKey)
                .claim("roles", userEntity.getRole())
                .compact();
        log.info("Auth-token {} сформирован", token);
        activeTokens.add(token);
        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            log.info("Token is valid!");
            return true;
        } catch (JwtException | IllegalArgumentException e) {

            log.error("Token validation failed");
            return false;
        }
    }

    public void removeToken(String token) {
        activeTokens.remove(token.substring(AuthenticationConfigConstants.TOKEN_PREFIX.length()));
    }
}