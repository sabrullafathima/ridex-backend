package com.project.ridex_backend.events.listener;

import com.project.ridex_backend.entity.Notification;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserType;
import com.project.ridex_backend.events.RideCompletedEvent;
import com.project.ridex_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideCompletedEventListener {
    private static final Logger logger = LoggerFactory.getLogger(RideCompletedEventListener.class);

    private final NotificationService notificationService;

    @EventListener
//   @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRideCompleted(RideCompletedEvent e) {
        logger.info("Listening to the RideCompletedEvent | rideId: {}", e.getRide().getId());

        sendRideCompletionNotification(
                e.getRide(),
                e.getRide().getDriver(),
                UserType.DRIVER,
                "You have completed a ride and earned LKR %s."
        );

        User recipient1 = e.getRide().getDriver();
        notificationService.sendEmailNotification(recipient1);

        sendRideCompletionNotification(
                e.getRide(),
                e.getRide().getRider(),
                UserType.RIDER,
                "Your ride is complete. You have paid LKR %s. Thank you for riding with Ridex!"
        );

        User recipient2 = e.getRide().getRider();
        notificationService.sendEmailNotification(recipient2);

    }

    private void sendRideCompletionNotification(Ride ride, User user, UserType type, String messageTemplate) {
        String message = String.format(messageTemplate, ride.getPayment().getAmount());
        Notification notification = notificationService.createNotificationForRideCompletion(ride, user, message);
        notificationService.sendNotification(notification, type);
    }
}
