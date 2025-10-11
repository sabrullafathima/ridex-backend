package com.project.ridex_backend.events.listener;

import com.project.ridex_backend.entity.Notification;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserType;
import com.project.ridex_backend.enums.UserRole;
import com.project.ridex_backend.events.RideRequestedEvent;
import com.project.ridex_backend.exception.RideNotFoundException;
import com.project.ridex_backend.repository.RideRepository;
import com.project.ridex_backend.repository.UserRepository;
import com.project.ridex_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RideRequestedEventListener {
    private static final Logger logger = LoggerFactory.getLogger(RideRequestedEventListener.class);

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final RideRepository rideRepository;

    @EventListener
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRideRequested(RideRequestedEvent e) {
        logger.info("Listening to the RideRequestedEvent | rideId: {}", e.getRideId());
        List<User> allDrivers = userRepository.findAllByRole(UserRole.DRIVER);
        List<User> nearbyDrivers = allDrivers.stream()
                // TODO: Add driver availability filtering for real-time assignment
                .limit(5)  // TODO: For demo, limiting to first 5 drivers; in production, can scale with proximity-based selection
                .toList();

        Ride ride = rideRepository.findById(e.getRideId()).orElseThrow(() -> new RideNotFoundException("Ride Not Found | rideId: " + e.getRideId()));
        for (User driver : nearbyDrivers) {
            Notification notification = notificationService.createNotificationForRideRequest(ride, driver);
            notificationService.sendNotification(notification, UserType.DRIVER);
        }

    }
}
