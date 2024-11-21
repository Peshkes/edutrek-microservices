package com.telran.groupservice.service;

import com.telran.groupservice.error.Exception;
import com.telran.groupservice.logging.Loggable;
import com.telran.groupservice.persistence.weekdays.WeekdayEntity;
import com.telran.groupservice.persistence.weekdays.WeekdayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeekdayService {

    private final WeekdayRepository repository;

    @Loggable
    public List<WeekdayEntity> getAll() {
        return repository.findAll();
    }

    @Loggable
    public WeekdayEntity getById(int weekdayId) {
        return repository.findById(weekdayId).orElseThrow(() -> new Exception.WeekdayNotFoundException(weekdayId));
    }
}
