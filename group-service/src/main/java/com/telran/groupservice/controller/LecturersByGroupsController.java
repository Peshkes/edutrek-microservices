package com.telran.groupservice.controller;

import com.telran.groupservice.service.LecturersByGroupsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lecturers_by_groups")
public class LecturersByGroupsController {

    private final LecturersByGroupsService service;

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/current/{lecturer_id}")
    public void deleteCurrentLecturersByLecturerId(@RequestParam UUID lecturer_id) {
        service.deleteCurrentLecturersByLecturerId(lecturer_id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/archive/{lecturer_id}")
    public void deleteArchiveLecturersByLecturerId(@RequestParam UUID lecturer_id) {
        service.deleteArchiveLecturersByLecturerId(lecturer_id);
    }
}
