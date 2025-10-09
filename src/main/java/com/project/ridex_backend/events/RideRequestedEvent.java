package com.project.ridex_backend.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RideRequestedEvent {
    private Long rideId;
}
