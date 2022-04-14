package com.jasik.momsnaggingapi.domain.diary.repository;

import com.jasik.momsnaggingapi.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
