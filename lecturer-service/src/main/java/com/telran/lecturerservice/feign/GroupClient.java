package com.telran.lecturerservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "GroupClient", url = "http://group-service:8080")
public interface GroupClient {
    @DeleteMapping("/lecturers_by_groups/current/{lecturer_id}")
    void deleteCurrentLecturersByLecturerId(@PathVariable UUID lecturer_id);

    @DeleteMapping("/lecturers_by_groups/archive/{lecturer_id}")
    void deleteArchiveLecturersByLecturerId(@PathVariable UUID lecturer_id);
}
