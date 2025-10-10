package com.project.ridex_backend.service;

import com.project.ridex_backend.entity.Notification;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.NotificationType;
import com.project.ridex_backend.enums.RecipientType;
import com.project.ridex_backend.repository.NotificationRepository;
import com.project.ridex_backend.websocket.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Notification createNotificationForRideRequest(Ride ride, User driver) {
        logger.info("Creating ride request notification | rideId: {}", ride.getId());

        String riderName = ride.getRider().getUsername();
        String pickupLocation = ride.getPickup();
        String message = String.format(
                "You have a new ride request from %s at %s.",
                riderName, pickupLocation
        );

        return createAndSaveNotification(
                NotificationType.RIDE_REQUEST,
                ride,
                driver,
                message);

    }

    public Notification createNotificationForRideAccept(Ride ride) {
        logger.info("Creating ride Accept notification | rideId: {}", ride.getId());

        String driverName = ride.getDriver().getUsername();
        String message = String.format(
                "Your ride has been accepted by %s. The driver is on the way",
                driverName
        );

       return createAndSaveNotification(
               NotificationType.RIDE_ACCEPTED,
               ride,
               ride.getRider(),
               message);

    }

    public Notification createAndSaveNotification(NotificationType type, Ride ride, User recipient, String message) {
        Notification notification = Notification.builder()
                .type(type)
                .ride(ride)
                .recipient(recipient)
                .message(message)
                .build();

        notificationRepository.save(notification);

        logger.info("Notification saved successfully | notificationId: {} | rideId: {} | recipientId: {}", notification.getId(), ride.getId(), recipient.getId());

        return notification;
    }

    public void sendNotification(Notification notify, RecipientType type) {
        Long recipientId = notify.getRecipient().getId();
        logger.info("Preparing NotificationPayload | {} Id: {} | rideId: {}", type, recipientId, notify.getRide().getId());

        NotificationPayload payload = NotificationPayload.builder()
                .notificationId(notify.getId())
                .rideId(notify.getRide().getId())
                .recipientId(notify.getRecipient().getId())
                .createdAt(notify.getCreatedAt())
                .type(notify.getType())
                .message(notify.getMessage())
                .build();

        logger.info("Sending Notification | {} Id: {}", type, recipientId);

        messagingTemplate.convertAndSend(
                "/topic/" + type.name().toLowerCase() + "/" + recipientId,
                MessageBuilder
                        .withPayload(payload)
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                        .build()
        );
    }

    public Notification createNotificationForRideCompletion(Ride ride, User recipient, String message) {
        logger.info("Creating ride Completed notification | rideId: {}", ride.getId());

        return createAndSaveNotification(
                NotificationType.RIDE_COMPLETED,
                ride,
                recipient,
                message);
    }
}
