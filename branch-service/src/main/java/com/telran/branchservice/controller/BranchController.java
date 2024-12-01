package com.telran.branchservice.controller;

import com.telran.branchservice.dto.BranchDataDto;
import com.telran.branchservice.persistence.BranchEntity;
import com.telran.branchservice.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<BranchEntity> getAllBranches() {
        return branchService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BranchEntity getBranchById(@PathVariable int id) {
        return branchService.getById(id);
    }

    @GetMapping("/name/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getBranchNameById(@PathVariable int id) {
        return branchService.getById(id).getBranchName();
    }

    @PostMapping("")
    public ResponseEntity<String> addNewBranch(@RequestBody @Valid BranchDataDto branchData) {
        branchService.addEntity(branchData);
        return new ResponseEntity<>("Branch created", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBranchById(@PathVariable int id) {
        branchService.deleteById(id);
        return new ResponseEntity<>("Branch deleted", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBranchById(@PathVariable int id, @RequestBody @Valid BranchDataDto branchData) {
        branchService.updateById(id, branchData);
        return new ResponseEntity<>("Branch updated", HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsBranchById(@PathVariable int id) {
        boolean result = branchService.existsById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
