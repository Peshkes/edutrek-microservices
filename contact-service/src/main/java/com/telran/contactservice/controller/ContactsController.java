package com.telran.contactservice.controller;


import com.telran.contactservice.dto.ContactSearchDto;
import com.telran.contactservice.dto.ContactsDataDto;
import com.telran.contactservice.dto.StudentsFromContactDataDto;
import com.telran.contactservice.persistence.AbstractContacts;
import com.telran.contactservice.service.ContactsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactsController {

    private final ContactsService contactsService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ContactSearchDto getAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pagesize", defaultValue = "10") int pageSize,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "statusid", required = false) Integer statusId,
            @RequestParam(name = "targetcourseid", required = false) UUID courseId
    ) {
        return contactsService.getAll(page, pageSize, search, statusId, courseId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AbstractContacts getById(@PathVariable UUID id) {
        return contactsService.getById(id);
    }

    @GetMapping("/find/{phone}/{email}")
    @ResponseStatus(HttpStatus.OK)
    public AbstractContacts findByPhoneOrEmail(@PathVariable String phone, @PathVariable String email) {
        return contactsService.findByPhoneOrEmail(phone, email);
    }
    @GetMapping("/exists/{phone}/{email}")
    @ResponseStatus(HttpStatus.OK)
    public boolean existsByPhoneOrEmail(@PathVariable String phone, @PathVariable String email) {
        return contactsService.existsByPhoneOrEmail(phone, email);
    }

    @DeleteMapping("/{phone}/{email}")
    @ResponseStatus(HttpStatus.OK)
    public AbstractContacts findByPhoneOrEmailAndDelete(@PathVariable String phone, @PathVariable String email) {
        return contactsService.findByPhoneOrEmailAndDelete(phone, email);
    }

    @PostMapping("")
    public ResponseEntity<String> addEntity(@RequestBody @Valid ContactsDataDto contactData) {
        contactsService.addEntity(contactData);
        return new ResponseEntity<>("Contact created", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        contactsService.deleteById(id);
        return new ResponseEntity<>("Contact deleted", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateById(@PathVariable UUID id, @RequestBody @Valid ContactsDataDto contactData) {
        contactsService.updateById(id, contactData);
        return new ResponseEntity<>("Contact updated", HttpStatus.OK);
    }

    @PutMapping("/archive/{id}/{reason}")
    public ResponseEntity<String> moveToArchiveById(@PathVariable UUID id, @PathVariable @DefaultValue("") String reason) {
        contactsService.moveToArchiveById(id, reason);
        return new ResponseEntity<>("Contact moved to archive", HttpStatus.OK);
    }

    @PostMapping("/promote/{id}")
    public ResponseEntity<String> promoteContactToStudentById(@PathVariable UUID id, @RequestBody @Valid StudentsFromContactDataDto studentData) {
        contactsService.promoteContactToStudentById(id, studentData);
        return new ResponseEntity<>("Contact promoted to student", HttpStatus.OK);
    }


}
