package com.telran.studentservice.dto;

import java.util.Collection;

public record StudentSearchDto(Collection<StudentWithGroupDto> students, int page, int pageSIze, long totalItems) {}