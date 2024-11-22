package com.telran.lecturerservice.controller;

import com.telran.lecturerservice.dto.LecturerDataDto;
import com.telran.lecturerservice.dto.LecturerPaginationResponseDto;
import com.telran.lecturerservice.persistence.BaseLecturer;
import com.telran.lecturerservice.service.LectureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LectureService service;

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    public LecturerPaginationResponseDto getAllPaginatedLecturers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return service.getAllPaginated(page, limit);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BaseLecturer getLecturerById(@PathVariable @UUID String id) {
            return service.getById(java.util.UUID.fromString(id));
    }

    @PostMapping("")
    public ResponseEntity<String> addNewLecturer(@RequestBody @Valid LecturerDataDto data) {
        service.addEntity(data);
        return new ResponseEntity<>("Lecturer created", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLecturerById(@PathVariable @UUID String id) {
            service.deleteById(java.util.UUID.fromString(id));
            return new ResponseEntity<>("Lecturer deleted", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateLecturerById(@PathVariable @UUID String id, @RequestBody @Valid LecturerDataDto data) {
            service.updateById(java.util.UUID.fromString(id), data);
            return new ResponseEntity<>("Lecturer updated", HttpStatus.OK);
    }

    @PutMapping("/archive/{id}")
    public ResponseEntity<String> archiveLecturerById(@PathVariable @UUID String id, @RequestBody String reason) {
        service.archiveById(java.util.UUID.fromString(id), reason);
        return new ResponseEntity<>("Lecturer archived", HttpStatus.OK);
    }

    @GetMapping("exists/{id}")
    public ResponseEntity<String> existsLecturerById(@PathVariable @UUID String id) {
        boolean exists = service.existsById(java.util.UUID.fromString(id));
        return new ResponseEntity<>(String.valueOf(exists), HttpStatus.OK);
    }
}
