package com.telran.groupservice.persistence.lessons_and_webinars_by_weekday;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LessonsByWeekdayRepository extends ISmthByWeekday<LessonsByWeekdayEntity> {
    void deleteByGroupId(@Param("id") UUID groupId);
    List<LessonsByWeekdayEntity>  getByGroupId(@Param("id") UUID uuid);
}
