package com.telran.studentservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class StudentsPromoteDataDto {
    @NotNull(message = "ID cannot be blank")
    UUID contactId;
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String contactName;
    @Pattern(regexp = "(\\+\\d{9,15})")
    private String phone;
    @Email
    private String email;
    @NotNull(message = "Status ID cannot be null")
    private int statusId;
    @NotNull(message = "Branch ID cannot be null")
    private int branchId;
    @NotNull(message = "Course ID cannot be null")
    private UUID targetCourseId;
    @Size(max = 256, message = "Name must be between 2 and 256 characters")
    private String comment;
    @NotNull(message = "Full payment cannot be null")
    private BigDecimal fullPayment;
    @NotNull(message = "Documents done cannot be null")
    private boolean documentsDone;
    private String logText = null;
}
