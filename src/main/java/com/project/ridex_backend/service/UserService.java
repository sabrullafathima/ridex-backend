package com.project.ridex_backend.service;

import com.project.ridex_backend.dto.UserResponse;
import com.project.ridex_backend.entity.UserRegisterRequest;
import com.project.ridex_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerNewUser(UserRegisterRequest request) {
        UserRegisterRequest user = UserRegisterRequest.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        UserRegisterRequest RegisteredUser = userRepository.save(user);
        logger.info("{} successfully registered.", RegisteredUser.getName());

        return UserResponse.builder()
                .id(RegisteredUser.getId())
                .name(RegisteredUser.getName())
                .email(RegisteredUser.getEmail())
                .role(RegisteredUser.getRole())
                .build();
    }

    public boolean existsByEmail(@Validated String email) {
        return userRepository.existsByEmail(email);
    }
}
