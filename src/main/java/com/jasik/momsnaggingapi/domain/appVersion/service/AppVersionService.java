package com.jasik.momsnaggingapi.domain.appVersion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppVersionService {
//    private final ReleaseRepository releaseRepository;
}
