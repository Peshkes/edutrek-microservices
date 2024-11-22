package com.telran.groupservice.persistence.groups;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(schema = "archive", name = "groups")
@NoArgsConstructor
public class GroupArchiveEntity extends BaseGroup {

    @Column(name = "archivation_date")
    private LocalDate archivationDate;

    public GroupArchiveEntity(BaseGroup groupEntity) {
        super(groupEntity);
        this.archivationDate = LocalDate.now();
    }
}
