package com.telran.groupservice.persistence.lessons_and_webinars_by_weekday;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WebinarsByWeekdayRepository extends ISmthByWeekday<WebinarsByWeekdayEntity>{
    @Modifying
    void deleteByGroupId(@Param("id") UUID groupId);
    List<WebinarsByWeekdayEntity> getByGroupId(@Param("id") UUID uuid);
}
