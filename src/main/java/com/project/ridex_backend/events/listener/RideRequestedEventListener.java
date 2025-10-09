package com.project.ridex_backend.events.listener;

import com.project.ridex_backend.entity.Notification;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserRole;
import com.project.ridex_backend.events.RideRequestedEvent;
import com.project.ridex_backend.exception.RideNotFoundException;
import com.project.ridex_backend.repository.RideRepository;
import com.project.ridex_backend.repository.UserRepository;
import com.project.ridex_backend.service.NotificationService;
import com.project.ridex_backend.websocket.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RideRequestedEventListener {
    private static final Logger logger = LoggerFactory.getLogger(RideRequestedEventListener.class);

    private final SimpMessagingTemplate messagingTemplate;
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
            logger.info("Sending ride request notification to driverId: {}", driver.getId());
            notifyDriver(notification);
        }

    }

    private void notifyDriver(Notification notify) {
        logger.info("Preparing NotificationPayload | driverId: {} | rideId: {}", notify.getRecipient().getId(), notify.getRide().getId());
        NotificationPayload payload = NotificationPayload.builder()
                .notificationId(notify.getId())
                .rideId(notify.getRide().getId())
                .createdAt(notify.getCreatedAt())
                .type(notify.getType())
                .message(notify.getMessage())
                .build();

        logger.info("Sending Notification | driverId: {}", notify.getRecipient().getId());
        messagingTemplate.convertAndSend(
                "/topic/driver/" + notify.getRecipient().getId(),
                MessageBuilder
                        .withPayload(payload)
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                        .build()
        );
    }
}
