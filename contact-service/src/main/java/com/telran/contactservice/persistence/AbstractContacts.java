package com.telran.contactservice.persistence;


import com.telran.contactservice.dto.AbstractStudentDto;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class AbstractContacts {
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

}
