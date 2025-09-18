package com.project.ridex_backend.security.jwt;

import com.project.ridex_backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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

    @Value("${JWT_EXPIRATION}")
    private long jwtExpirationMs;

    public JwtService(SecretKey key) {
        this.key = key;
    }

    public String generateAccessToken(User registeredUser) {
        logger.info("Generating JWT token for user: {}", registeredUser.getUsername());
        Map<String, Object> claims = Map.of(ROLE, registeredUser.getRole().toString());

        return Jwts.builder()
                .addClaims(claims)
                .setSubject(registeredUser.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            logger.error("Invalid JWT Token");
            return false;
        }
    }

    public String extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(ROLE, String.class); // why it map with string class when we create it we did Map.of(ROLE, registeredUser.getRole().toString()); like this

    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
