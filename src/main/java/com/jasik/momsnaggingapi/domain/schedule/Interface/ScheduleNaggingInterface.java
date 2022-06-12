package com.jasik.momsnaggingapi.domain.schedule.Interface;

import com.jasik.momsnaggingapi.domain.nagging.Nagging;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;

public interface ScheduleNaggingInterface {
    Schedule getSchedule();
    Nagging getNagging();
}
