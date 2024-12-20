package com.telran.studentservice.controller;


import com.telran.studentservice.dto.*;
import com.telran.studentservice.persistence.AbstractStudent;
import com.telran.studentservice.service.StudentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentsController {

    private final StudentsService studentService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public StudentSearchDto getAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pagesize", defaultValue = "10") int pageSize,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "statusid", required = false) Integer statusId,
            @RequestParam(name = "groupid", required = false) UUID groupId,
            @RequestParam(name = "targetcourseid", required = false) UUID courseId) {
        return studentService.getAll(page,pageSize,search,statusId,groupId, courseId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AbstractStudent getById(@PathVariable UUID id) {
        return studentService.getById(id);
    }

    @GetMapping("/exists/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean existsById(@PathVariable UUID id) {
        return studentService.existsById(id);
    }

    @GetMapping("/find/{phone}/{email}")
    @ResponseStatus(HttpStatus.OK)
    public AbstractStudent findByPhoneOrEmailAndDelete(@PathVariable String phone, @PathVariable String email) {
        return studentService.findByPhoneOrEmailAndDelete(phone, email);
    }

    @PostMapping("/find_students")
    @ResponseStatus(HttpStatus.OK)
    public List<AbstractStudent> findStudents(@RequestBody FindStudentsDto findStudentsDto) {
        return studentService.findStudents(findStudentsDto.getPageable(),findStudentsDto.getSearch(), findStudentsDto.getStatusId(), findStudentsDto.getGroup_id(), findStudentsDto.getCourseId(),findStudentsDto.isCurrentRepository());
    }

    @PostMapping("/find_students_contacts")
    @ResponseStatus(HttpStatus.OK)
    public List<AbstractStudent> findStudentsForContacts(@RequestBody FindStudentsForContactsDto findStudentsDto) {
        return studentService.findStudentForContacts(findStudentsDto.getQuantity(),findStudentsDto.getSearch(), findStudentsDto.getStatusId(), findStudentsDto.getGroup_id(), findStudentsDto.getCourseId(),findStudentsDto.isCurrentRepository());
    }

    @PostMapping("/promote")
    public ResponseEntity<String> promoteEntity(@RequestBody @Valid StudentsDataDto studentsDataDto) {
        studentService.promoteEntity(studentsDataDto);
        return new ResponseEntity<>("Student created", HttpStatus.CREATED);
    }

    @PostMapping("")
    public ResponseEntity<String> addEntity(@RequestBody @Valid StudentsDataDto studentDto) {
        studentService.addEntity(studentDto);
        return new ResponseEntity<>("Student created", HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        studentService.deleteById(id);
        return new ResponseEntity<>("Student deleted", HttpStatus.OK);
    }


    @PutMapping("")
    public ResponseEntity<String> updateById(@RequestBody @Valid StudentsDataDto contactData) {
        studentService.updateById(contactData);
        return new ResponseEntity<>("Student updated", HttpStatus.OK);
    }

    @PutMapping("/archive/{id}/{reason}")
    public ResponseEntity<String> moveToArchiveById(@PathVariable UUID id,@PathVariable @DefaultValue("") String reason) {
        studentService.moveToArchiveById(id, reason);
        return new ResponseEntity<>("Student moved to archive", HttpStatus.OK);
    }

    @PutMapping("/graduate/{id}")
    public ResponseEntity<String> graduateById(@PathVariable UUID id) {
        studentService.graduateById(id);
        return new ResponseEntity<>("Student moved to archive", HttpStatus.OK);
    }


}
