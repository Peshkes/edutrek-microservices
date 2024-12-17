package com.telran.notificationservice.dto;

import com.telran.notificationservice.persistence.AbstractNotificationDocument;
import com.telran.notificationservice.persistence.INotificationsRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  TargetEntityDataDto<T extends AbstractNotificationDocument> {
    private INotificationsRepository<T> repository;
    private BiFunction<UUID, List<NotificationDataDto>, T> constructor;
}
