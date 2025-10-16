package com.project.ridex_backend.events.listener;

import com.project.ridex_backend.entity.Notification;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserType;
import com.project.ridex_backend.events.RideStartedEvent;
import com.project.ridex_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideStartedEventListener {
    private static final Logger logger = LoggerFactory.getLogger(RideStartedEventListener.class);
    private final NotificationService notificationService;

    @EventListener
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRideStarted(RideStartedEvent e) {
        logger.info("Listening to RideStartedEvent | rideId: {}", e.getRide().getId());

        String message = "Your ride has started. Enjoy your trip!";
        Notification notification = notificationService.createNotificationForRideStart(e.getRide(), e.getRide().getRider(), message);
        notificationService.sendNotification(notification, UserType.RIDER);

        User recipient = e.getRide().getRider();
        notificationService.sendEmailNotification(recipient);

    }
}
