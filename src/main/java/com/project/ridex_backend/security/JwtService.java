package com.project.ridex_backend.security;

import com.project.ridex_backend.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final String ROLE = "role";
    private final SecretKey key;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public JwtService(SecretKey key) {
        this.key = key;
    }

    public String generateAccessToken(User registeredUser) {
        logger.info("Generating JWT token for user: {}", registeredUser.getUsername());
        Map<String, Object> claims = Map.of(ROLE, registeredUser.getRole());

        return Jwts.builder()
                .addClaims(claims)
                .setSubject(registeredUser.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
