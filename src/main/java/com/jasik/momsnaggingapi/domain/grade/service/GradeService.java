package com.jasik.momsnaggingapi.domain.grade.service;

import com.jasik.momsnaggingapi.domain.grade.repository.GradeRepository;
import com.jasik.momsnaggingapi.domain.nagging.repository.NaggingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
}
