package com.telran.groupservice.controller;

import com.telran.groupservice.dto.AddGroupDto;
import com.telran.groupservice.dto.ChangeLecturersDto;
import com.telran.groupservice.dto.GetStudentsByGroupDto;
import com.telran.groupservice.dto.PaginationGroupResponseDto;
import com.telran.groupservice.persistence.groups.BaseGroup;
import com.telran.groupservice.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BaseGroup getGroupById(@PathVariable @UUID String id) {
        return groupService.getById(java.util.UUID.fromString(id));
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    public PaginationGroupResponseDto getAllGroupsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @UUID String filter_course,
            @RequestParam(required = false) Boolean filter_is_active,
            @RequestParam(required = false) String search
    ) {
        return groupService.getAllPaginated(page, limit, filter_course, filter_is_active, search);
    }

    @GetMapping("/groups/students")
    @ResponseStatus(HttpStatus.OK)
    public List<GetStudentsByGroupDto> getStudentsByGroup(@RequestBody @UUID Set<java.util.UUID> ids) {
        return groupService.getStudentsByGroup(ids);
    }


    @PostMapping("")
    public ResponseEntity<String> addNewGroup(@RequestBody @Valid AddGroupDto groupData) {
        groupService.addEntity(groupData);
        return new ResponseEntity<>("Group created", HttpStatus.CREATED);
    }

    @PostMapping("/students/{id}")
    public ResponseEntity<String> addStudentsToGroup(@PathVariable @UUID String id, @RequestBody List<java.util.UUID> students) {
        groupService.addStudentsToGroup(java.util.UUID.fromString(id), students);
        return new ResponseEntity<>("Students added", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroupById(@PathVariable @UUID String id) {
        groupService.deleteById(java.util.UUID.fromString(id));
        return new ResponseEntity<>("Group deleted", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateGroupById(@PathVariable @UUID String id, @RequestBody @Valid AddGroupDto groupData) {
        groupService.updateById(java.util.UUID.fromString(id), groupData);
        return new ResponseEntity<>("Group updated", HttpStatus.OK);
    }

    @PutMapping("/graduate/{id}")
    public ResponseEntity<String> graduateGroupById(@PathVariable @UUID String id) {
        groupService.graduateById(java.util.UUID.fromString(id));
        return new ResponseEntity<>("Group graduated", HttpStatus.OK);
    }

    @PutMapping("/{fromId}/move/{toId}")
    public ResponseEntity<String> moveStudentsBetweenGroups(@PathVariable @UUID String fromId, @PathVariable @UUID String toId, @RequestBody List<java.util.UUID> students) {
        groupService.moveStudentsBetweenGroups(java.util.UUID.fromString(fromId), java.util.UUID.fromString(toId), students);
        return new ResponseEntity<>("Group created", HttpStatus.CREATED);
    }

    @PutMapping("/archive/students/{id}")
    public ResponseEntity<String> archiveStudents(@PathVariable @UUID String id, @RequestBody @Valid List<java.util.UUID> students) {
        groupService.archiveStudents(java.util.UUID.fromString(id), students);
        return new ResponseEntity<>("Students archived", HttpStatus.OK);
    }

    @PutMapping("/archive/studentid/{id}")
    public ResponseEntity<String> archiveStudents(@PathVariable @UUID String id) {
        groupService.archiveStudentsByStudentId(java.util.UUID.fromString(id));
        return new ResponseEntity<>("Students archived", HttpStatus.OK);
    }

    @DeleteMapping("/delete/studentid/{id}/{iscurrent}")
    public ResponseEntity<String> deleteByStudentId(@PathVariable @UUID String id, @PathVariable boolean iscurrent) {
        groupService.deleteByStudentId(java.util.UUID.fromString(id), iscurrent);
        return new ResponseEntity<>("Students deleted from groups", HttpStatus.OK);
    }

    @PutMapping("/lecturers/{id}")
    public ResponseEntity<String> changeLecturersToGroup(@PathVariable @UUID String id, @RequestBody @Valid List<ChangeLecturersDto> lecturers) {
        groupService.changeLecturersToGroup(java.util.UUID.fromString(id), lecturers);
        return new ResponseEntity<>("Lecturers added", HttpStatus.CREATED);
    }
}
