package com.telran.groupservice.controller;

import com.telran.groupservice.persistence.weekdays.WeekdayEntity;
import com.telran.groupservice.service.WeekdayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weekdays")
@RequiredArgsConstructor
public class WeekdayController {

    private final WeekdayService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<WeekdayEntity> getAllStatuses() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WeekdayEntity getById(@PathVariable int id) {
            return service.getById(id);
    }
}
