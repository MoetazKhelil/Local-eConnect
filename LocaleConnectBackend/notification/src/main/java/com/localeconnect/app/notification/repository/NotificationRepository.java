package com.localeconnect.app.notification.repository;

import com.localeconnect.app.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIDAndIsRead(Long receiverId, boolean isRead);

}
