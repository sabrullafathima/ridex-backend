package com.project.ridex_backend.events.listener;

import com.project.ridex_backend.entity.Notification;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserType;
import com.project.ridex_backend.events.RideAcceptedEvent;
import com.project.ridex_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideAcceptedEventListener {
    private static final Logger logger = LoggerFactory.getLogger(RideAcceptedEventListener.class);

    private final NotificationService notificationService;

    @EventListener
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRideAccepted(RideAcceptedEvent e) {
        Long rideId = e.getRide().getId();
        Long driverId = e.getRide().getDriver().getId();
        logger.info("Listening RideAcceptedEvent | rideId: {}, driverId: {}", rideId, driverId);

        Notification notify = notificationService.createNotificationForRideAccept(e.getRide());

        notificationService.sendNotification(notify, UserType.RIDER);

        User recipient = e.getRide().getRider();
        notificationService.sendEmailNotification(recipient);
    }
}
