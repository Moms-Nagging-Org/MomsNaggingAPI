package com.jasik.momsnaggingapi.domain.nagging.service;

import com.jasik.momsnaggingapi.domain.nagging.repository.NaggingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NaggingService {
    private final NaggingRepository naggingRepository;
}
