package com.project.ridex_backend.controller;//package com.project.ridex_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile() {
        return ResponseEntity.ok("This is your protected profile data");
    }
}
