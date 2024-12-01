package com.telran.lecturerservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import static com.telran.lecturerservice.error.ValidationErrors.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturerDataDto {

    @NotNull(message = NAME_MANDATORY)
    @NotBlank(message = NAME_NOT_EMPTY)
    @Size(min = 2, max = 100, message = NAME_SIZE)
    private String lecturerName;

    @NotNull(message = PHONE_MANDATORY)
    @NotBlank(message = PHONE_NOT_EMPTY)
    @Pattern(regexp = "^(?:\\+972|0)([23489]|5[0-9])-?\\d{7}$",
            message = PHONE_INVALID_FORMAT)
    private String phone;

    @NotNull(message = EMAIL_MANDATORY)
    @NotBlank(message = EMAIL_NOT_EMPTY)
    @Email(message = EMAIL_INVALID_FORMAT)
    private String email;

    @NotNull(message = BRANCH_MANDATORY)
    private int branchId;

    @Size(max = 255, message = COMMENT_SIZE)
    private String comment;

    private String logText = null;
}