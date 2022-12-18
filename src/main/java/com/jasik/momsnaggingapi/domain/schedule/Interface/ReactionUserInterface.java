package com.jasik.momsnaggingapi.domain.schedule.Interface;

import com.jasik.momsnaggingapi.domain.nagging.Nagging;
import com.jasik.momsnaggingapi.domain.schedule.Reaction;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.user.User;

public interface ReactionUserInterface {
    Reaction getReaction();
    User getUser();
}
