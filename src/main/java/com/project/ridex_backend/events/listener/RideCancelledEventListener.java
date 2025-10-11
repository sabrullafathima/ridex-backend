package com.project.ridex_backend.events.listener;

import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserType;
import com.project.ridex_backend.events.RideCancelledEvent;
import com.project.ridex_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideCancelledEventListener {
    private static final Logger logger = LoggerFactory.getLogger(RideCancelledEventListener.class);

    private final NotificationService notificationService;

    @EventListener
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRideCancellation(RideCancelledEvent e) {
        logger.info("Listening to RideCancelledEvent | rideId: {}", e.getRide().getId());

        if (UserType.RIDER.name().equalsIgnoreCase(String.valueOf(e.getCancelBy()))) {
            notificationService.createNotificationForRideCancellationByRider(e.getRide(), e.getRideStatusBeforeCancel());
        } else if (UserType.DRIVER.name().equalsIgnoreCase(String.valueOf(e.getCancelBy()))) {
            notificationService.createNotificationForRideCancellationByDriver(e.getRide());
        }
    }
}
