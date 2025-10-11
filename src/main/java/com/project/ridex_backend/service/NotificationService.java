package com.project.ridex_backend.service;

import com.project.ridex_backend.email.EmailService;
import com.project.ridex_backend.entity.Notification;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.NotificationType;
import com.project.ridex_backend.enums.RideStatus;
import com.project.ridex_backend.enums.UserType;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;

    public Notification createNotificationForRideRequest(Ride ride, User driver) {
        String riderName = ride.getRider().getUsername();
        String pickupLocation = ride.getPickup();
        String message = String.format(
                "You have a new ride request from %s at %s.",
                riderName, pickupLocation
        );

        return createAndSaveNotification(NotificationType.RIDE_REQUEST, ride, driver, message);
    }

    public void createNotificationForRideCancellationByRider(Ride ride, RideStatus rideStatusBeforeCancel) {
        String message = String.format(
                "Ride request from %s has been cancelled.",
                ride.getRider().getUsername()
        );

        if (rideStatusBeforeCancel.equals(RideStatus.REQUESTED)) {
            notifyAllRequestedDrivers(ride, message);
        } else {
            notifyAcceptedDriver(ride, message);
        }
    }

    private void notifyAcceptedDriver(Ride ride, String message) {
        User driver = ride.getDriver();

        if (driver == null) {
            logger.warn("No driver assigned for rideId: {} | Skipping driver notification", ride.getId());
            return;
        }

        sendRideCancellationNotification(ride, driver, message);
    }

    private void notifyAllRequestedDrivers(Ride ride, String message) {
        logger.info("Finding sent notification list for rideId: {}", ride.getId());
        List<Notification> sentNotifications = notificationRepository.findByRideId(ride.getId());

        if (sentNotifications == null || sentNotifications.isEmpty()) {
            //TODO: need to handle this scenario
            logger.warn("No drivers were notified for rideId: {} | Skipping cancellation notifications", ride.getId());
            return;
        }

        sentNotifications.forEach(sentNotification -> {
                    User driver = sentNotification.getRecipient();
                    sendRideCancellationNotification(ride, driver, message);
                });
    }

    private void sendRideCancellationNotification(Ride ride, User driver, String message) {
        Notification notification = createAndSaveNotification(
                NotificationType.RIDE_CANCELLED, ride, driver, message
        );

        sendNotification(notification, UserType.DRIVER);
    }


    public void createNotificationForRideCancellationByDriver(Ride ride) {
        User rider = ride.getRider();
        String message = "Your driver has cancelled the ride. Please request a new one.";

        Notification notification = createAndSaveNotification(NotificationType.RIDE_CANCELLED, ride, rider, message);
        sendNotification(notification, UserType.RIDER);

    }


    public Notification createNotificationForRideAccept(Ride ride) {
        logger.info("Creating ride Accept notification | rideId: {}", ride.getId());

        String driverName = ride.getDriver().getUsername();
        String message = String.format(
                "Your ride has been accepted by %s. The driver is on the way",
                driverName
        );

        return createAndSaveNotification(NotificationType.RIDE_ACCEPTED, ride, ride.getRider(), message);
    }


    public Notification createNotificationForRideStart(Ride ride, User recipient, String message) {
        logger.info("Creating ride start notification | rideId: {}", ride.getId());

        return createAndSaveNotification(NotificationType.RIDE_STARTED, ride, recipient, message);
    }


    public Notification createNotificationForRideCompletion(Ride ride, User recipient, String message) {
        logger.info("Creating ride Completed notification | rideId: {}", ride.getId());

        return createAndSaveNotification(NotificationType.RIDE_COMPLETED, ride, recipient, message);
    }


    public Notification createAndSaveNotification(NotificationType type, Ride ride, User recipient, String message) {
        logger.info("Creating {} notification for {} | rideId: {}", type, recipient.getRole(), ride.getId());

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


    public void sendNotification(Notification notify, UserType type) {
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

    public void sendEmailNotification(User recipient) {
        emailService.sendEmail(
                recipient.getEmail(),
                "New Ride Request",
                "Hello " + recipient.getUsername() + ", you have a new ride request"
        );
    }
}
