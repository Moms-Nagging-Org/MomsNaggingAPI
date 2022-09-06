package com.jasik.momsnaggingapi.domain.push.Interface;

import com.jasik.momsnaggingapi.domain.nagging.Nagging;
import com.jasik.momsnaggingapi.domain.push.Push;

public interface PushNaggingInterface {
    Push getPush();
    Nagging getNagging();
}
