package com.telran.contactservice.dto;

import com.telran.contactservice.persistence.current.ContactsEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class StudentsDataDto {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String contactName;
    @Pattern(regexp = "(\\+\\d{9,15})?")
    private String phone;
    @Pattern(regexp = "(.[^,\\s]+@(\\w[^_#]+\\.)+[a-z]{2,12})?")
    private String email;
    private int statusId;
    private int branchId;
    private UUID targetCourseId;
    @Size(max = 256, message = "Name must be between 2 and 256 characters")
    private String comment;
    private int fullPayment;
    private boolean documentsDone;


    public StudentsDataDto(ContactsEntity contact, StudentsFromContactDataDto studentData) {
        this.contactName = contact.getContactName();
        this.phone = contact.getPhone();
        this.email = contact.getEmail();
        this.statusId = contact.getStatusId();
        this.branchId = contact.getBranchId();
        this.targetCourseId = contact.getTargetCourseId();
        this.comment = contact.getComment();
        this.fullPayment = studentData.getFullPayment();
        this.documentsDone = studentData.isDocumentsDone();
    }
}
