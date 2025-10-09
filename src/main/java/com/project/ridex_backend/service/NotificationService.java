package com.project.ridex_backend.service;

import com.project.ridex_backend.entity.Notification;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.NotificationType;
import com.project.ridex_backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;

    public Notification createNotificationForRideRequest(Ride ride, User driver) {
        logger.info("Creating ride request notification | rideId: {}", driver.getId());

        String riderName = ride.getRider().getUsername();
        String pickupLocation = ride.getPickup();
        String message = String.format(
                "You have a new ride request from %s at %s.",
                riderName, pickupLocation
        );

        Notification notification = Notification.builder()
                .recipient(driver)
                .message(message)
                .ride(ride)
                .type(NotificationType.RIDE_REQUEST)
                .build();
        notificationRepository.save(notification);

        logger.info("Notification saved successfully | notificationId: {} | rideId: {} | driverId: {}", notification.getId(), ride.getId(), driver.getId());

        return notification;

    }
}
