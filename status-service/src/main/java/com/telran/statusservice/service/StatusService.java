package com.telran.statusservice.service;

import com.telran.statusservice.dto.StatusDataDto;
import com.telran.statusservice.error.DatabaseException.*;
import com.telran.statusservice.error.ShareException.*;
import com.telran.statusservice.logging.Loggable;
import com.telran.statusservice.persistence.StatusEntity;
import com.telran.statusservice.persistence.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"statuses"})
public class StatusService {

    private final StatusRepository repository;

    @Loggable
    @Cacheable(key = "{'all'}")
    public List<StatusEntity> getAll() {
        return repository.findAll();
    }

    @Loggable
    @Cacheable(key = "#id")
    public StatusEntity getById(int id) {
        return repository.findById(id).orElseThrow(() -> new StatusNotFoundException(id));
    }

    @Loggable
    @Cacheable(key = "#name")
    public int getIdByName(String name) {
        StatusEntity status = repository.findByStatusName(name).orElseThrow(() -> new StatusNameNotFoundException(name));
        return status.getStatusId();
    }

    @Loggable
    @Transactional
    @CacheEvict(key = "{'all'}")
    public void addEntity(StatusDataDto statusData) {
        try {
            repository.save(new StatusEntity(statusData.getStatusName()));
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "{'all'}"),
            @CacheEvict(key = "'exist:' + #id")
    })
    public void deleteById(int id) {
        if (!repository.existsById(id)) throw new StatusNotFoundException(id);
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
            @CacheEvict(key = "{'all'}"),
            @CacheEvict(key = "'exist:' + #id")
    })
    public void updateById(int id, String newName) { //заменил на стринг
        StatusEntity status = repository.findById(id).orElseThrow(() -> new StatusNotFoundException(id));
        status.setStatusName(newName);
        try {
            repository.save(status);
        } catch (Exception e) {
            throw new DatabaseUpdatingException(e.getMessage());
        }
    }

    @Loggable
    @Cacheable(key = "'exist:' + #id")
    public boolean existsById(int id) {
        return repository.existsById(id);
    }

    @Loggable
    //@Cacheable(key = "#status")
    public StatusEntity findStatusEntityByStatusName(String status) {
        return repository.findStatusEntityByStatusName(status);
    }

}
