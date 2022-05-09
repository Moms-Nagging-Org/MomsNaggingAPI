package com.jasik.momsnaggingapi.domain.diary.repository;

import com.jasik.momsnaggingapi.domain.diary.Diary;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByUserIdAndDiaryDate(Long userId, LocalDate retrieveDate);
}
