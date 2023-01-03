package com.jasik.momsnaggingapi.domain.user.Interface;

import com.jasik.momsnaggingapi.domain.follow.Follow;
import com.jasik.momsnaggingapi.domain.user.User;

public interface UserFollowInterface {
    User getUser();
    Follow getFollow();
}
