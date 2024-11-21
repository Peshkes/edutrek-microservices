package com.telran.contactservice.share;

import com.telran.contactservice.error.ShareException;
import com.telran.contactservice.persistence.BranchRepository;
import com.goodquestion.edutrek_server.modules.course.persistence.CourseRepository;
import com.goodquestion.edutrek_server.modules.statuses.persistence.StatusRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CommonUtilityMethods {

    public static void checkStatusCourseBranch(int branchId, UUID targetCourseId, int statusId, BranchRepository branchRepository, CourseRepository courseRepository, StatusRepository statusRepository){
        if (!branchRepository.existsById(branchId))
            throw new ShareException.BranchNotFoundException(String.valueOf(branchId));
        if (!courseRepository.existsById(targetCourseId))
            throw new ShareException.CourseNotFoundException(String.valueOf(targetCourseId));
        if (!statusRepository.existsById(statusId))
            throw new ShareException.CourseNotFoundException(String.valueOf(statusId));
    }
}
