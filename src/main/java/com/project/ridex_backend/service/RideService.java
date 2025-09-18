package com.project.ridex_backend.service;

import com.project.ridex_backend.dto.request.RideRequest;
import com.project.ridex_backend.dto.response.RideResponse;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.RideStatus;
import com.project.ridex_backend.enums.UserRole;
import com.project.ridex_backend.exception.AccessDeniedException;
import com.project.ridex_backend.exception.DriverNotAvailableException;
import com.project.ridex_backend.exception.RideNotFoundException;
import com.project.ridex_backend.repository.RideRepository;
import com.project.ridex_backend.repository.UserRepository;
import com.project.ridex_backend.utils.ResponseMapper;
import com.project.ridex_backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RideService {
    private static final Logger logger = LoggerFactory.getLogger(RideService.class);

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final SecurityUtil securityUtil;

    public RideService(UserRepository userRepository, RideRepository rideRepository, SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.securityUtil = securityUtil;
    }

    public RideResponse requestRide(RideRequest request) {
        logger.info("Processing ride request | pickup: {} destination: {}", request.getPickup(), request.getDestination());

        validateUserRole(UserRole.RIDER);

        User rider = securityUtil.extractCurrentUser();
        logger.debug("Authenticated rider | userId: {}", rider.getId());

        // TODO: extend with select driver by near and available
        User driver = findAvailableDriver();
        logger.debug("Selected driver | userId: {}", driver.getId());

        Ride ride = createRide(rider, driver, request);
        rideRepository.save(ride);
        logger.info("Ride saved successfully | rideId: {}", ride.getId());

        //Current API: directly assigns a driver and returns ride info — rider sees assigned driver immediately.
        //TODO: the API should create a ride request without a driver, notify driver(s), and update ride when a driver accepts.

        RideResponse rideResponse = ResponseMapper.toRideResponse(ride);
        logger.debug("RideResponse prepared: {}", rideResponse);
        return rideResponse;
    }

    private User findAvailableDriver() {
        return userRepository.findFirstByRole(UserRole.DRIVER)
                .orElseThrow(() -> {
                    logger.warn("No available drivers | at: {}", LocalDateTime.now());
                    return new DriverNotAvailableException("No drivers available at the moment");
                });
    }

    private Ride createRide(User rider, User driver, RideRequest request) {
        logger.debug("Creating Ride entity | riderId: {} driverId: {}", rider.getId(), driver.getId());
        //TODO: should create an api to send request to driver;

        return Ride.builder()
                .rider(rider)
                .driver(driver)
                .pickup(request.getPickup())
                .destination(request.getDestination())
                .status(RideStatus.REQUESTED.name())  //TODO: handle actual driver response logic
                .build();
    }

    private void validateUserRole(UserRole requiredRole) {
        boolean haRequiredRole = securityUtil.extractCurrentUserRole(requiredRole);
        if (!haRequiredRole) {
            logger.warn("Access denied | Required role: {} ", requiredRole);
            throw new AccessDeniedException(requiredRole + " role required to perform this action");
        }
    }

    private void validateRider(Authentication auth) {

    }

    public RideResponse acceptRide(@Valid Long rideId) {
        validateUserRole(UserRole.DRIVER);
        logger.info("Find ride | rideId : {}", rideId);
        Ride ride = findRideById(rideId);
        ride.setDriver(securityUtil.extractCurrentUser());
        ride.setStatus(RideStatus.ACCEPTED.name());
        logger.debug("Ride updated | DriverId: {}, Status: {}", ride.getDriver(), ride.getStatus());
        rideRepository.save(ride);
        logger.info("Ride successfully saved -> DB");

        RideResponse rideResponse = ResponseMapper.toRideResponse(ride);
        //TODO: send notification to the requested rider;
        logger.debug("Updated rideResponse | ACCEPTED, rideResponse: {}", rideResponse);
        return rideResponse;
    }

    public RideResponse completeRide(Long rideId) {
        validateUserRole(UserRole.DRIVER);
        Ride ride = findRideById(rideId);
        ride.setStatus(RideStatus.COMPLETED.name());
        rideRepository.save(ride);

        RideResponse rideResponse = ResponseMapper.toRideResponse(ride);
        //TODO: send notification to the rider;
        logger.debug("Updated rideResponse status | COMPLETED, rideResponse: {}", rideResponse);
        return rideResponse;
    }

    private Ride findRideById(Long rideId) {
        return rideRepository.findById(rideId).orElseThrow(() -> {
            logger.error("Ride NOT_FOUND | rideId: {}", rideId);
            return new RideNotFoundException("Ride NOT_FOUND | rideId: " + rideId);
        });
    }
}
