package com.project.ridex_backend.repository;

import com.project.ridex_backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientId(Long driverId);

    List<Notification> findByRideId(Long id);
}
