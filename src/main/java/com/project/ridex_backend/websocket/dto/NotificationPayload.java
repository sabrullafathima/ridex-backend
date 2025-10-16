package com.project.ridex_backend.websocket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.ridex_backend.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationPayload {
    private Long notificationId;
    private Long rideId;
    private Long recipientId;
    private String message;
    private LocalDateTime createdAt;
    private NotificationType type;
}

