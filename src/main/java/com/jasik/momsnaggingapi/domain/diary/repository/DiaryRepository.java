package com.jasik.momsnaggingapi.domain.diary.repository;

import com.jasik.momsnaggingapi.domain.diary.Diary;
import com.jasik.momsnaggingapi.domain.diary.Diary.DailyDiary;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByUserIdAndDiaryDate(Long userId, LocalDate retrieveDate);

    @Transactional(readOnly = true)
    @Query(name = "findDailyDiary", nativeQuery = true)
    List<DailyDiary> findDiaryOfPeriodByUserIdAndStartDateAndEndDate(
        @Param("userId") Long userId, @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}
