package com.jasik.momsnaggingapi.domain.push.service;

import com.jasik.momsnaggingapi.domain.push.repository.PushRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PushService {
//    private final PushRepository pushRepository;
}
