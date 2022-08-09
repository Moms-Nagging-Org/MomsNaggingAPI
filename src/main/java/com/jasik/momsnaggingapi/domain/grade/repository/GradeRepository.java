package com.jasik.momsnaggingapi.domain.grade.repository;

import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.Grade.Performance;
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

    long countByUserIdAndGradeLevel(Long userId, int gradeLevel);

    List<Grade> findByGradeLevel(int gradeLevel);

    List<Grade> findAllByUserIdAndCreatedYearAndCreatedWeekGreaterThanEqualAndCreatedWeekLessThanEqualOrderByCreatedYearAscCreatedWeekAsc(
        Long userId, int createdYear, int startCreatedWeek, int endCreatedWeek);

    @Transactional(readOnly = true)
    @Query(name = "findStatisticsResponse", nativeQuery = true)
    Grade.StatisticsResponse findStatisticsByUserId(@Param("userId") Long userId);

    @Transactional(readOnly = true)
    @Query(name = "findPerformanceOfPeriod", nativeQuery = true)
    List<Performance> findPerformanceOfPeriodByUserIdAndStartDateAndEndDate(
        @Param("userId") Long userId, @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}
