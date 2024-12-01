package com.telran.statusservice.controller;

import com.telran.statusservice.dto.StatusDataDto;
import com.telran.statusservice.service.StatusService;
import com.telran.statusservice.persistence.StatusEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statuses")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<StatusEntity> getAllStatuses() {
        return statusService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StatusEntity getStatusById(@PathVariable int id) {
            return statusService.getById(id);
    }

    @GetMapping("/name/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getStatusNameById(@PathVariable int id) {
            return statusService.getById(id).getStatusName();
    }

    @PostMapping("")
    public ResponseEntity<String> addNewStatus(@RequestBody @Valid StatusDataDto statusData) {
        statusService.addEntity(statusData);
        return new ResponseEntity<>("Status created", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStatusById(@PathVariable int id) {
            statusService.deleteById(id);
            return new ResponseEntity<>("Status deleted", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateStatusById(@PathVariable int id, @RequestBody @Valid StatusDataDto statusData) {
            statusService.updateById(id, statusData.getStatusName());
            return new ResponseEntity<>("Status updated", HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean existsById(@PathVariable int id) {
        return statusService.existById(id);
    }

    @GetMapping("/find_by_status_name/{status}")
    @ResponseStatus(HttpStatus.OK)
    public StatusEntity findStatusEntityByStatusName(@PathVariable String status) {
        return statusService.findStatusEntityByStatusName(status);
    }

}
