package com.telran.courseservice.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(schema = "current", name = "courses")
@Getter
@NoArgsConstructor
public class CourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "course_id")
    private UUID courseId;
    @Setter
    @Column(name = "course_name")
    private String courseName;
    @Setter
    @Column(name = "course_abbreviation")
    private String courseAbbreviation;

    public CourseEntity(String courseName, String courseAbbreviation) {
        this.courseName = courseName;
        this.courseAbbreviation = courseAbbreviation;
    }
}
