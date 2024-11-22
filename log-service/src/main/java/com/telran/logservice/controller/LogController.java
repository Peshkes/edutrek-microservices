package com.telran.logservice.controller;

import com.telran.logservice.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getLogsById(@PathVariable UUID id) {
            return service.getById(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> addNewLogById(@PathVariable UUID id, @RequestBody String log) {
        service.add(id, log);
        return new ResponseEntity<>("Log added", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        service.deleteById(id);
        return new ResponseEntity<>("Logs deleted", HttpStatus.OK);
    }
}
