package com.telran.statusservice.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "current", name = "statuses")
@Getter
@NoArgsConstructor
public class StatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private int statusId;
    @Setter
    @Column(name = "status_name", unique = true)

    private String statusName;

    public StatusEntity(String statusName) {
        this.statusName = statusName;
    }
}
