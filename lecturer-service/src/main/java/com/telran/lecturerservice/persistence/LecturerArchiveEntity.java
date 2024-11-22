package com.telran.lecturerservice.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(schema = "archive", name = "lecturers")
@NoArgsConstructor
public class LecturerArchiveEntity extends BaseLecturer {
    @Column(name = "reason_of_archivation")
    private String reasonOfArchivation;
    @Column(name = "archivation_date")
    private LocalDate archivationDate;

    public LecturerArchiveEntity(BaseLecturer lecturer, String reasonOfArchivation) {
        super(lecturer);
        this.reasonOfArchivation = reasonOfArchivation;
        this.archivationDate = LocalDate.now();
    }
}
