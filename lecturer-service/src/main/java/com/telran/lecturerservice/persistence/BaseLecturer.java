package com.telran.lecturerservice.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseLecturer {
    @Id
    @Column(name = "lecturer_id")
    private UUID lecturerId;
    @Setter
    @Column(name = "lecturer_name")
    private String lecturerName;
    @Setter
    @Column(name = "phone")
    private String phone;
    @Setter
    @Column(name = "email")
    private String email;
    @Setter
    @Column(name = "branch_id")
    private int branchId;
    @Setter
    @Column(name = "comment")
    private String comment;

    public BaseLecturer(String lecturerName, String phone, String email, int branchId, String comment) {
        this.lecturerId = UUID.randomUUID();
        this.lecturerName = lecturerName;
        this.phone = phone;
        this.email = email;
        this.branchId = branchId;
        this.comment = comment;
    }

    public BaseLecturer(BaseLecturer baseLecturer) {
        this.lecturerId = baseLecturer.getLecturerId();
        this.lecturerName = baseLecturer.getLecturerName();
        this.phone = baseLecturer.getPhone();
        this.email = baseLecturer.getEmail();
        this.branchId = baseLecturer.getBranchId();
        this.comment = baseLecturer.getComment();
    }
}
