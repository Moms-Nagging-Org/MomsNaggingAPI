package com.jasik.momsnaggingapi.domain.diary.service;

import com.jasik.momsnaggingapi.domain.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {
//    private final DiaryRepository diaryRepository;
}
