package com.telran.statusservice.persistence;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface StatusRepository extends JpaRepository<StatusEntity, Integer> {

    StatusEntity findStatusEntityByStatusName(String status);
}
