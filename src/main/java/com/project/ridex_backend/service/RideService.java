package com.project.ridex_backend.service;

import com.project.ridex_backend.dto.request.PaymentRequest;
import com.project.ridex_backend.dto.request.RideRequest;
import com.project.ridex_backend.dto.response.RideResponse;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.PaymentMethod;
import com.project.ridex_backend.enums.RideStatus;
import com.project.ridex_backend.enums.UserRole;
import com.project.ridex_backend.events.RideRequestedEvent;
import com.project.ridex_backend.exception.AccessDeniedException;
import com.project.ridex_backend.exception.RideNotFoundException;
import com.project.ridex_backend.repository.RideRepository;
import com.project.ridex_backend.utils.ResponseMapper;
import com.project.ridex_backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideService {
    private static final Logger logger = LoggerFactory.getLogger(RideService.class);

    private final RideRepository rideRepository;
    private final SecurityUtil securityUtil;
    private final PaymentService paymentService;
    private final ApplicationEventPublisher eventPublisher;

    public RideResponse requestRide(RideRequest request) {
        logger.info("Processing ride request | pickup: {} destination: {}", request.getPickup(), request.getDestination());

        validateUserRole(UserRole.RIDER);

        User rider = securityUtil.extractCurrentUser();
        logger.info("Authenticated rider | riderId: {}", rider.getId());

        Ride ride = createRide(rider, request);
        rideRepository.save(ride);
        logger.info("Saved Ride To DB successfully | rideId: {}", ride.getId());

        eventPublisher.publishEvent(new RideRequestedEvent(ride.getId()));

        return ResponseMapper.toRideResponse(ride);
    }

    private Ride createRide(User rider, RideRequest request) {
        logger.debug("Creating Ride entity | riderId: {}", rider.getId());

        double estimatedFare = paymentService.calculateEstimatedFareForDemo(request.getPickup(), request.getDestination());

        return Ride.builder()
                .rider(rider)
                .driver(null)
                .pickup(request.getPickup())
                .destination(request.getDestination())
                .status(RideStatus.REQUESTED)
                .estimatedFare(estimatedFare)
                .payment(null)
                .build();
    }

    private void validateUserRole(UserRole requiredRole) {
        boolean hasRequiredRole = securityUtil.extractCurrentUserRole(requiredRole);
        if (!hasRequiredRole) {
            logger.warn("Access denied | Required role: {} ", requiredRole);
            throw new AccessDeniedException(requiredRole + " role required to perform this action");
        }
    }

    public RideResponse acceptRide(@Valid Long rideId) {
        validateUserRole(UserRole.DRIVER);
        logger.info("Find ride | rideId : {}", rideId);
        Ride ride = findRideById(rideId);
        ride.setDriver(securityUtil.extractCurrentUser());
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setPayment(paymentService.createPayment(ride));
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
        ride.setStatus(RideStatus.COMPLETED);
        rideRepository.save(ride);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .pickupLocation(ride.getPickup())
                .dropLocation(ride.getDestination())
                .paymentMethod(PaymentMethod.CARD)
                .build();
        paymentService.processPayment(ride, paymentRequest);

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

    public List<RideResponse> getRidesForDrivers() {
        validateUserRole(UserRole.DRIVER);
        Long driverId = securityUtil.extractCurrentUser().getId();
        logger.info("RIDES_REQUEST | START | driverId: {}", driverId);

        List<Ride> rides = rideRepository.findRideByStatus(RideStatus.REQUESTED);
        if (rides.isEmpty()) {
            logger.info("RIDES_NOT_FOUND | status: REQUESTED");
            return null;
        }
        logger.debug("IDES_FOUND | count: {} | rideIds: {}",
                rides.size(),
                rides.stream().map(Ride::getId).collect(Collectors.toList()));

        return rides.stream()
                .map(ResponseMapper::toRideResponse)
                .collect(Collectors.toList());

    }

    public RideResponse getRidesForCurrentDriver() {
        validateUserRole(UserRole.DRIVER);
        Long driverId = securityUtil.extractCurrentUser().getId();
        logger.info("CURRENT_RIDE_REQUEST | START | driverId: {}", driverId);
        Ride ride = rideRepository.findRideByStatusAndDriverId(RideStatus.ACCEPTED, driverId);
        if (ride == null) {
            logger.debug("CURRENT_RIDE_FOUND | driverId: {}", driverId);
            return null;
        }
        return ResponseMapper.toRideResponse(ride);

    }
}
