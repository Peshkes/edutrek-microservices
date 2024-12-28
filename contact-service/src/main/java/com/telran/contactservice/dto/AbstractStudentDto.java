package com.telran.contactservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractStudentDto {
    private UUID studentId;
    private String contactName;
    private String phone;
    private String email;
    private int statusId;
    private int branchId;
    private UUID targetCourseId;
    private String comment;
    private int fullPayment;
    private boolean documentsDone;
    private List<GroupsDto> groups;
    private int amountAlreadyPayed;

//    public AbstractStudentDto(UUID studentId, String contactName, String phone, String email, int statusId, int branchId, UUID targetCourseId, String comment, int fullPayment, boolean documentsDone, List<GroupsDto> groups, int amountAlreadyPayed) {
//        this.studentId = studentId;
//        this.contactName = contactName;
//        this.phone = phone;
//        this.email = email;
//        this.statusId = statusId;
//        this.branchId = branchId;
//        this.targetCourseId = targetCourseId;
//        this.comment = comment;
//        this.fullPayment = fullPayment;
//        this.documentsDone = documentsDone;
//        this.groups = groups == null ? new ArrayList<>() : groups;
//        this.amountAlreadyPayed = amountAlreadyPayed;
//    }
}
