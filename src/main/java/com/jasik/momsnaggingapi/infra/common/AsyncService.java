package com.jasik.momsnaggingapi.infra.common;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
    @Async
    public void run(Runnable runnable) { runnable.run(); }

}