package com.telran.courseservice.controller;

import com.telran.courseservice.dto.CourseDataDto;
import com.telran.courseservice.persistence.CourseEntity;
import com.telran.courseservice.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<CourseEntity> getAllCourses() {
        return courseService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CourseEntity getCourseById(@PathVariable @UUID String id) {
        return courseService.getById(java.util.UUID.fromString(id));
    }

    @PostMapping("")
    public ResponseEntity<String> addNewCourse(@RequestBody @Valid CourseDataDto courseData) {
        courseService.addEntity(courseData);
        return new ResponseEntity<>("Course created", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourseById(@PathVariable @UUID String id) {
        courseService.deleteById(java.util.UUID.fromString(id));
        return new ResponseEntity<>("Course deleted", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourseById(@PathVariable @UUID String id, @RequestBody @Valid CourseDataDto courseData) {
        courseService.updateById(java.util.UUID.fromString(id), courseData);
        return new ResponseEntity<>("Course updated", HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsCourseById(@PathVariable @UUID String id) {
        boolean result = courseService.existsById(java.util.UUID.fromString(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
