package com.telran.groupservice.persistence.lessons_and_webinars_by_weekday;

import com.telran.groupservice.key.ComposeWeekdayKey;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ComposeWeekdayKey.class)
@MappedSuperclass
public class BaseSmthByWeekday {
    @Id
    @Column(name = "group_id")
    private UUID groupId;
    @Id
    @Column(name = "weekday_id")
    private int weekdayId;

    public BaseSmthByWeekday(BaseSmthByWeekday smthByWeekdayEntity) {
        this.groupId = smthByWeekdayEntity.getGroupId();
        this.weekdayId = smthByWeekdayEntity.getWeekdayId();
    }
}
