package com.project.ridex_backend.service;

import com.project.ridex_backend.dto.request.RideRequest;
import com.project.ridex_backend.dto.response.RideResponse;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserRole;
import com.project.ridex_backend.exception.DriverNotAvailableException;
import com.project.ridex_backend.exception.UnauthorizedRideRequestException;
import com.project.ridex_backend.repository.RideRepository;
import com.project.ridex_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RideService {
    private static final Logger logger = LoggerFactory.getLogger(RideService.class);

    private final UserRepository userRepository;

    private final RideRepository rideRepository;

    public RideService(UserRepository userRepository, RideRepository rideRepository) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
    }

    public RideResponse requestRide(RideRequest request) {
        logger.info("Processing ride request from user");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        validateRider(auth);

        User rider = extractRider(auth);
        logger.debug("Authenticated riderId: {}", rider.getId());

        // TODO: extend with select driver by near and available
        User driver = findAvailableDriver();
        logger.debug("Selected driverId: {}", driver.getId());

        Ride ride = CreateRide(rider, driver, request);
        rideRepository.save(ride);
        logger.info("Ride saved successfully with rideId: {}", ride.getId());

        //Current API: directly assigns a driver and returns ride info — rider sees assigned driver immediately.
        //TODO: the API should create a ride request without a driver, notify driver(s), and update ride when a driver accepts.

        RideResponse rideResponse = buildRideResponse(ride);
        logger.debug("RideResponse prepared: {}", rideResponse);
        return rideResponse;
    }

    private RideResponse buildRideResponse(Ride ride) {
        return RideResponse.builder()
                .riderId(ride.getRider().getId().toString())
                .driverId(ride.getDriver().getId().toString())
                .pickup(ride.getPickup())
                .destination(ride.getDestination())
                .status(ride.getStatus())
                .build();
    }

    private Ride CreateRide(User rider, User driver, RideRequest request) {
        logger.debug("Creating Ride entity for riderId: {} and driverId: {}", rider.getId(), driver.getId());
        //TODO: should create an api to send request to driver;

        String status = "PENDING_DRIVER"; //TODO: handle actual driver response logic

        return Ride.builder()
                .rider(rider)
                .driver(driver)
                .pickup(request.getPickup())
                .destination(request.getDestination())
                .status(status)
                .build();

    }

    private User findAvailableDriver() {
        return userRepository.findFirstByRole(UserRole.DRIVER)
                .orElseThrow(() -> {
                    logger.error("No available drivers found");
                    return new DriverNotAvailableException("No drivers available right now");
                });
    }

    private User extractRider(Authentication auth) {
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

    private void validateRider(Authentication auth) {
        boolean isRider = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(UserRole.RIDER.toString()));

        if (!isRider) {
            logger.error("Unauthorized ride request attempt by non-rider");
            throw new UnauthorizedRideRequestException("Only riders can request rides");
        }
    }
}
