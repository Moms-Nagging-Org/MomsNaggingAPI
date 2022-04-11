package com.jasik.momsnaggingapi.domain.schedule.repository;

import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
