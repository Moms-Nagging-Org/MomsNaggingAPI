package com.jasik.momsnaggingapi.domain.release.service;

import com.jasik.momsnaggingapi.domain.release.repository.ReleaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReleaseService {
    private final ReleaseRepository releaseRepository;
}
