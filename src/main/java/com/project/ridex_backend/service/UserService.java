package com.project.ridex_backend.service;

import com.project.ridex_backend.dto.request.UserLoginRequest;
import com.project.ridex_backend.dto.request.UserRegisterRequest;
import com.project.ridex_backend.dto.response.AuthResponse;
import com.project.ridex_backend.dto.response.UserResponse;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.exception.InvalidLoginDetailsException;
import com.project.ridex_backend.repository.UserRepository;
import com.project.ridex_backend.security.jwt.JwtService;
import com.project.ridex_backend.utils.ResponseMapper;
import com.project.ridex_backend.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecurityUtil securityUtil;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.securityUtil = securityUtil;
    }

    public UserResponse registerNewUser(UserRegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        user = userRepository.save(user);
        logger.info("{} successfully registered.", user.getUsername());

        return ResponseMapper.toUserResponse(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public AuthResponse loginUser(UserLoginRequest request) {
        User registeredUser = userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .orElseThrow(() -> new InvalidLoginDetailsException("Invalid username or password"));

        logger.info("User {} successfully logged in", registeredUser.getUsername());

        String accessToken = jwtService.generateAccessToken(registeredUser);

        return new AuthResponse(accessToken);
    }

    public UserResponse getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = securityUtil.extractCurrentUser();
        return ResponseMapper.toUserResponse(user);
    }

    public UserResponse getUserDetailsById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User not found with userId: {}", userId);
            return new RuntimeException("User not found");
        });

        return ResponseMapper.toUserResponse(user);
    }

}
