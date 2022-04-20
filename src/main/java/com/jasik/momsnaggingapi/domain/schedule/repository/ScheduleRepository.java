package com.jasik.momsnaggingapi.domain.schedule.repository;

import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Collection<Schedule> findAllByScheduleDateAndUserId(String scheduleId, Long userId);
}
