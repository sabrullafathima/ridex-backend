package com.project.ridex_backend.controller;

import com.project.ridex_backend.entity.Notification;
import com.project.ridex_backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationRepository notificationRepository;

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Notification>> getNotificationsForDriver(@PathVariable Long driverId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(driverId);
        if (notifications.isEmpty()) {
            logger.info("Notification not found");
            return null;
        }

        logger.info("Notification found message: {}", notifications.getFirst().getMessage());

        return ResponseEntity.ok(notifications);
    }
}
