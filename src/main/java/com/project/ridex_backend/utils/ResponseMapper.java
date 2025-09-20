package com.project.ridex_backend.utils;

import com.project.ridex_backend.dto.response.RideResponse;
import com.project.ridex_backend.dto.response.UserResponse;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;

public class ResponseMapper {
    public static RideResponse toRideResponse(Ride ride) {
        return RideResponse.builder()
                .rideId(ride.getId())
                .riderId(ride.getRider().getId().toString())
                .driverId(ride.getDriver().getId().toString())
                .pickup(ride.getPickup())
                .destination(ride.getDestination())
                .status(ride.getStatus().name())
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
