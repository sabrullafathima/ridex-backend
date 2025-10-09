package com.project.ridex_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class RideResponse {
    private Long rideId;
    private Long riderId;
    private Long driverId;
    private String pickup;
    private String destination;
    private String status;
    private double estimatedFare;
    private PaymentResponse payment;
}
