package com.telran.studentservice.dto;

import com.telran.studentservice.persistence.AbstractStudent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithGroupDto {
    private UUID studentId;
    private String contactName;
    private String phone;
    private String email;
    private int statusId;
    private int branchId;
    private UUID targetCourseId;
    private String comment;
    private double fullPayment;
    private boolean documentsDone;
    private List<GroupsDto> groups;
    private double amountAlreadyPayed;

    public StudentWithGroupDto(AbstractStudent abstractStudent) {
        this.studentId = abstractStudent.getStudentId();
        this.contactName = abstractStudent.getContactName();
        this.phone = abstractStudent.getPhone();
        this.email = abstractStudent.getEmail();
        this.statusId = abstractStudent.getStatusId();
        this.branchId = abstractStudent.getBranchId();
        this.targetCourseId = abstractStudent.getTargetCourseId();
        this.comment = abstractStudent.getComment();
        this.fullPayment = abstractStudent.getFullPayment();
        this.documentsDone = abstractStudent.isDocumentsDone();
        this.groups = new ArrayList<>();
    }
}
