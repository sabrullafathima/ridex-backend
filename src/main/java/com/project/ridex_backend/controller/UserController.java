package com.project.ridex_backend.controller;

import com.project.ridex_backend.dto.request.RideRequest;
import com.project.ridex_backend.dto.response.RideResponse;
import com.project.ridex_backend.service.RideService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final RideService rideService;

    public UserController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile() {
        return ResponseEntity.ok("Hello User This is Your Profile");
    }

    @PostMapping("/rides/request")
    public ResponseEntity<RideResponse> requestRide(@Valid @RequestBody RideRequest rideRequest) {
        logger.info("Received ride request from user");
        RideResponse rideResponse = rideService.requestRide(rideRequest);
        logger.info("Ride request processed successfully for riderId: {}", rideResponse.getRiderId());
        return ResponseEntity.ok(rideResponse);
    }

}
