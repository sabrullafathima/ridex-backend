package com.project.ridex_backend.events;

import com.project.ridex_backend.entity.Ride;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RideStartedEvent {
    private Ride ride;
}
