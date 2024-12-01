package com.telran.groupservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddGroupDto {
    @NotBlank(message = "Group name cannot be empty")
    @Size(max = 100, message = "Group name must not exceed 100 characters")
    private String groupName;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "Finish date cannot be null")
    private LocalDate finishDate;

    @NotNull(message = "Activity status cannot be null")
    private Boolean isActive;

    @NotNull(message = "Course ID cannot be null")
    private UUID courseId;

    @Pattern(regexp = "^https?://.*$", message = "Slack link must be a valid URL")
    private String slackLink;

    @Pattern(regexp = "^https?://.*$", message = "WhatsApp link must be a valid URL")
    private String whatsAppLink;

    @Pattern(regexp = "^https?://.*$", message = "Skype link must be a valid URL")
    private String skypeLink;

    @NotNull(message = "Deactivate after cannot be null")
    private Boolean deactivateAfter;

    @NotNull(message = "Lessons cannot be null")
    private List<@Min(value = 1, message = "Lessons cannot be less than 1" )
    @Max(value = 7, message = "Lessons cannot be greater than 7" ) Integer> lessons;

    @NotNull(message = "Webinars cannot be null")
    private List<@Min(value = 1, message = "Webinars cannot be less than 1" )
    @Max(value = 7, message = "Webinars cannot be greater than 7" ) Integer> webinars;

    @NotNull(message = "Lecturers cannot be null")
    private List<@Valid ChangeLecturersDto> lecturers;
}