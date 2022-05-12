package com.jasik.momsnaggingapi.domain.grade.repository;

import com.jasik.momsnaggingapi.domain.grade.Grade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    Optional<Grade> findByCreatedYearAndCreatedWeekAndUserId(int createdYear, int createdWeek,
        Long userId);
    long countByUserIdAndGradeLevel(Long userId, int gradeLevel);
}
