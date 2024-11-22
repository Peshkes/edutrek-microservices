package com.telran.groupservice.persistence.lessons_and_webinars_by_weekday;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Entity
@Getter
@Table(schema = "current", name = "webinars_by_weekday")
public class WebinarsByWeekdayEntity extends BaseSmthByWeekday{
    public WebinarsByWeekdayEntity(BaseSmthByWeekday webinarsByWeekdayEntity) {
        super(webinarsByWeekdayEntity);
    }

    public WebinarsByWeekdayEntity(UUID groupId, int weekdayId) {
        super(groupId, weekdayId);
    }
}
