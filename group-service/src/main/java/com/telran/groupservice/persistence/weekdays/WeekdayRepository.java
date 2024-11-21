package com.telran.groupservice.persistence.weekdays;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeekdayRepository extends JpaRepository<WeekdayEntity, Integer> {}