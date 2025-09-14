package com.project.ridex_backend.controller;

import com.project.ridex_backend.dto.UserResponse;
import com.project.ridex_backend.entity.UserRegisterRequest;
import com.project.ridex_backend.exception.UserAlreadyExistsException;
import com.project.ridex_backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        logger.info("{} user started to register", request.getName());

        if (userService.existsByEmail(request.getEmail())) {
            logger.error("{} this email already exists", request.getEmail());
            throw new UserAlreadyExistsException("User with this email already exists!");
        }

        UserResponse userResponse = userService.registerNewUser(request);

        return ResponseEntity.ok(userResponse);
    }
}
