package com.localeconnect.app.notification.mapper;

import com.localeconnect.app.notification.dto.NotificationDTO;
import com.localeconnect.app.notification.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDTO toDomain(Notification notification);

    Notification toEntity(NotificationDTO notificationDTO);
}
