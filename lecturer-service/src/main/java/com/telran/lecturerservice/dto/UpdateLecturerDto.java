package com.telran.lecturerservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import static com.telran.lecturerservice.error.ValidationErrors.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLecturerDto {

    @Size(min = 2, max = 100, message = NAME_SIZE)
    private String lecturerName;

    @Pattern(regexp = "(\\+\\d{9,15})?", message = PHONE_INVALID_FORMAT)
    private String phone;

    @Email(message = EMAIL_INVALID_FORMAT)
    private String email;
    private Integer branchId;

    @Size(max = 255, message = COMMENT_SIZE)
    private String comment;
    private String logText = null;
}