package com.jasik.momsnaggingapi.domain.grade.repository;

import com.jasik.momsnaggingapi.domain.grade.Grade;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    Optional<Grade> findByCreatedYearAndCreatedWeekAndUserId(int createdYear, int createdWeek,
        Long userId);
}
