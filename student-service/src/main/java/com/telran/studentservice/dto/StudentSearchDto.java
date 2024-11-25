package com.telran.studentservice.dto;
import com.telran.studentservice.persistence.AbstractStudent;

import java.util.List;

public record StudentSearchDto(List<AbstractStudent> students, int page, int pageSIze, long totalItems) {}