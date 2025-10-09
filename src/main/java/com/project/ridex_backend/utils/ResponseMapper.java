package com.project.ridex_backend.utils;

import com.project.ridex_backend.dto.response.PaymentResponse;
import com.project.ridex_backend.dto.response.RideResponse;
import com.project.ridex_backend.dto.response.UserResponse;
import com.project.ridex_backend.entity.Payment;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseMapper {
    private static final Logger logger = LoggerFactory.getLogger(ResponseMapper.class);

    public static RideResponse toRideResponse(Ride ride) {
        logger.info("Preparing RideResponse for | rideId: {}", ride.getId());

        return RideResponse.builder()
                .rideId(ride.getId())
                .riderId(ride.getRider().getId())
                .driverId(ride.getDriver() != null ? ride.getDriver().getId() : null)
                .pickup(ride.getPickup())
                .destination(ride.getDestination())
                .status(ride.getStatus().name())
                .estimatedFare(ride.getEstimatedFare())
                .payment(toPaymentResponse(ride.getPayment(), ride.getId()))
                .build();
    }

    private static PaymentResponse toPaymentResponse(Payment payment, Long rideId) {
        logger.info("Preparing Payment Details for | rideId: {}", rideId);
        if (payment == null) {
            return null;
        }
        return PaymentResponse.builder()
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
