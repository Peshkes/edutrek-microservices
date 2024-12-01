package com.telran.branchservice.service;

import com.telran.branchservice.error.DatabaseException.*;
import com.telran.branchservice.error.BranchNotFoundException;
import com.telran.branchservice.dto.BranchDataDto;
import com.telran.branchservice.logging.Loggable;
import com.telran.branchservice.persistence.BranchEntity;
import com.telran.branchservice.persistence.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames={"branches"})
public class BranchService {

    private final BranchRepository repository;

    @Loggable
    @Cacheable(key = "{'getALl'}")
    public List<BranchEntity> getAll() {
        return repository.findAll();
    }

    @Loggable
    @Cacheable(key = "#id")
    public BranchEntity getById(int id) {
        return repository.findById(id).orElseThrow(() -> new BranchNotFoundException(String.valueOf(id)));
    }

    @Loggable
    @CacheEvict(key = "{'getALl'}")
    @Transactional
    public void addEntity(BranchDataDto branchData) {
        try {
            repository.save(new BranchEntity(branchData.getBranchName(), branchData.getBranchAddress()));
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
    public void deleteById(int id) {
        if (!repository.existsById(id)) throw new BranchNotFoundException(String.valueOf(id));

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
    public void updateById(int id, BranchDataDto branchData) {
        BranchEntity branchEntity = repository.findById(id).orElseThrow(() -> new BranchNotFoundException(String.valueOf(id)));
        branchEntity.setBranchName(branchData.getBranchName());
        branchEntity.setBranchAddress(branchData.getBranchAddress());
        try {
            repository.save(branchEntity);
        } catch (Exception e) {
            throw new DatabaseUpdatingException(e.getMessage());
        }
    }

    @Loggable
    @Cacheable(key = "'exist:' + #id")
    public boolean existsById(int id) {
        return repository.existsById(id);
    }
}