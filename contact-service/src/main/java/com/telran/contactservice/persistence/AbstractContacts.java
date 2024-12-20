package com.telran.contactservice.persistence;


import com.telran.contactservice.dto.AbstractStudentDto;
import com.telran.contactservice.dto.ContactsDataDto;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractContacts {
    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "contact_id")
    private UUID contactId;
    @Column(name = "contact_name")
    private String contactName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "status_id")
    private int statusId;
    @Column(name = "branch_id")
    private int branchId;
    @Column(name = "target_course_id")
    private UUID targetCourseId;
    @Column(name = "comment")
    private String comment;

    public AbstractContacts(AbstractStudentDto abstractStudent) {
        this.contactId = abstractStudent.getStudentId();
        this.contactName = abstractStudent.getContactName();
        this.phone = abstractStudent.getPhone();
        this.email = abstractStudent.getEmail();
        this.statusId = abstractStudent.getStatusId();
        this.branchId = abstractStudent.getBranchId();
        this.targetCourseId = abstractStudent.getTargetCourseId();
        this.comment = abstractStudent.getComment();
    }

    public AbstractContacts(AbstractContacts abstractContacts) {
        this.contactId = abstractContacts.getContactId();
        this.contactName = abstractContacts.getContactName();
        this.phone = abstractContacts.getPhone();
        this.email = abstractContacts.getEmail();
        this.statusId = abstractContacts.getStatusId();
        this.branchId = abstractContacts.getBranchId();
        this.targetCourseId = abstractContacts.getTargetCourseId();
        this.comment = abstractContacts.getComment();
    }

    public AbstractContacts(String contactName, String phone, String email, int statusId, int branchId, UUID targetCourseId, String comment) {
        this.contactId = UUID.randomUUID();
        this.contactName = contactName;
        this.phone = phone;
        this.email = email;
        this.statusId = statusId;
        this.branchId = branchId;
        this.targetCourseId = targetCourseId;
        this.comment = comment;
    }

    public AbstractContacts(ContactsDataDto contactsDataDto) {
        this.contactId = UUID.randomUUID();
        this.contactName = contactsDataDto.getContactName();
        this.phone = contactsDataDto.getPhone();
        this.email = contactsDataDto.getEmail();
        this.statusId = contactsDataDto.getStatusId();
        this.branchId = contactsDataDto.getBranchId();
        this.targetCourseId = contactsDataDto.getTargetCourseId();
        this.comment = contactsDataDto.getComment();
    }

}
