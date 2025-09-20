package com.project.ridex_backend.controller;

import com.project.ridex_backend.dto.request.RideRequest;
import com.project.ridex_backend.dto.response.RideResponse;
import com.project.ridex_backend.dto.response.UserResponse;
import com.project.ridex_backend.service.RideService;
import com.project.ridex_backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final RideService rideService;
    private final UserService userService;

    public UserController(RideService rideService, UserService userService) {
        this.rideService = rideService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile() {
        return ResponseEntity.ok("Hello User This is Your Profile");
    }

    @GetMapping("/details")
    public ResponseEntity<UserResponse> getCurrentUserDetails() {
        UserResponse userResponse = userService.getCurrentUserDetails();
        logger.info("Fetching current user details | userId: {}", userResponse.getId());
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/rides")
    public ResponseEntity<List<RideResponse>> getRidesForDrivers() {
        List<RideResponse> rides = rideService.getRidesForDrivers();
        return rides != null
                ? ResponseEntity.ok(rides)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/rides/current")
    public ResponseEntity<RideResponse> getRidesForCurrentDriver() {
        RideResponse ride = rideService.getRidesForCurrentDriver();
        return ride!= null
                ? ResponseEntity.ok(ride)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/details/{userId}")
    public ResponseEntity<UserResponse> getUserDetailsById(@Valid @PathVariable Long userId) {
        UserResponse userResponse = userService.getUserDetailsById(userId);
        logger.info("successfully fetch the userId details : {}", userId);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/rides/request")
    public ResponseEntity<RideResponse> requestRide(@Valid @RequestBody RideRequest rideRequest) {
        logger.info("Received ride request from user");
        RideResponse rideResponse = rideService.requestRide(rideRequest);
        logger.info("Ride request processed successfully for riderId: {}", rideResponse.getRiderId());
        return ResponseEntity.ok(rideResponse);
    }

    @PostMapping("/rides/{rideId}/accept")
    public ResponseEntity<RideResponse> acceptRide(@Valid @PathVariable Long rideId) {
        logger.info("Received ride request | rideId: {}: ", rideId);
        RideResponse rideResponse = rideService.acceptRide(rideId);
        logger.info("Ride accepted | rideId: {}, driverId: {}", rideId, rideResponse.getDriverId());
        return ResponseEntity.ok(rideResponse);
    }

    @PostMapping("/rides/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(@Valid @PathVariable Long rideId) {
        RideResponse rideResponse = rideService.completeRide(rideId);
        return ResponseEntity.ok(rideResponse);
    }
}
