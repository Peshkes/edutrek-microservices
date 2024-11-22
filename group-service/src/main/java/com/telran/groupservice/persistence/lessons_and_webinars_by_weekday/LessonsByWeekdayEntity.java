package com.telran.groupservice.persistence.lessons_and_webinars_by_weekday;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Entity
@Getter
@Table(schema = "current", name = "lessons_by_weekday")
public class LessonsByWeekdayEntity extends BaseSmthByWeekday{
    public LessonsByWeekdayEntity(LessonsByWeekdayEntity lessonsByWeekdayEntity) {
        super(lessonsByWeekdayEntity);
    }

    public LessonsByWeekdayEntity(UUID groupId, int weekdayId) {
        super(groupId, weekdayId);
    }
}
