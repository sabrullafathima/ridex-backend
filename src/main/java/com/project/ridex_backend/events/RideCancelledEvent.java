package com.project.ridex_backend.events;


import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.enums.RideStatus;
import com.project.ridex_backend.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RideCancelledEvent {
    private Ride ride;
    private UserType cancelBy;
    private RideStatus rideStatusBeforeCancel;

}
