package com.project.ridex_backend.utils;

import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserRole;
import com.project.ridex_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private final UserRepository userRepository;

    public SecurityUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User extractCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();
        long userId;
        if (principal instanceof String) {
            userId = Long.parseLong((String) principal);
        } else if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            logger.error("Invalid principal type: {}", principal.getClass().getName());
            throw new RuntimeException("Invalid principal type");
        }

        return userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User not found for userId: {}", userId);
            return new RuntimeException("User not found");
        });
    }

    public boolean extractCurrentUserRole(UserRole requiredRole) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role-> role.equals(requiredRole.name()));
    }
}
