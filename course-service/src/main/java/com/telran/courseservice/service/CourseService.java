package com.telran.courseservice.service;

import com.telran.courseservice.dto.CourseDataDto;
import com.telran.courseservice.error.CourseNotFoundException;
import com.telran.courseservice.error.DatabaseException.DatabaseAddingException;
import com.telran.courseservice.error.DatabaseException.DatabaseDeletingException;
import com.telran.courseservice.error.DatabaseException.DatabaseUpdatingException;
import com.telran.courseservice.logging.Loggable;
import com.telran.courseservice.persistence.CourseEntity;
import com.telran.courseservice.persistence.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames={"courses"})
public class CourseService {

    private final CourseRepository repository;

    @Loggable
    @Cacheable(key = "{'getALl'}")
    public List<CourseEntity> getAll() {
        return repository.findAll();
    }

    @Loggable
    @Cacheable(key = "#courseId")
    public CourseEntity getById(UUID courseId) {
        return repository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(String.valueOf(courseId)));
    }

    @Loggable
    @Transactional
    @CacheEvict(key = "{'getAll'}")
    public void addEntity(CourseDataDto courseData) {
        try {
            repository.save(new CourseEntity(courseData.getCourseName(), courseData.getCourseAbbreviation()));
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'exist:' + #id"),
            @CacheEvict(key = "'getAll'")
    })
    public void deleteById(UUID id) {
        if (!repository.existsById(id))
            throw new CourseNotFoundException(String.valueOf(id));

        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'exist:' + #id"),
            @CacheEvict(key = "'getAll'")
    })
    public void updateById(UUID id, CourseDataDto courseData) {
        CourseEntity courseEntity = repository.findById(id).orElseThrow(() -> new CourseNotFoundException(String.valueOf(id)));

        courseEntity.setCourseName(courseData.getCourseName());
        courseEntity.setCourseAbbreviation(courseData.getCourseAbbreviation());
        try {
            repository.save(courseEntity);
        } catch (Exception e) {
            throw new DatabaseUpdatingException(e.getMessage());
        }
    }

    @Loggable
    @Cacheable(key = "'exist:' + #id")
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }
}
