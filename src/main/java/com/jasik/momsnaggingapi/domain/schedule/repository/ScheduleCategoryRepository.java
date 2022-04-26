package com.jasik.momsnaggingapi.domain.schedule.repository;

import com.jasik.momsnaggingapi.domain.schedule.ScheduleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ScheduleCategoryRepository extends JpaRepository<ScheduleCategory, Long> {
}
