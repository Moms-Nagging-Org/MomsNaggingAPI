package com.jasik.momsnaggingapi.domain.push.repository;

import com.jasik.momsnaggingapi.domain.push.Push;
import com.jasik.momsnaggingapi.domain.push.Push.PushType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushRepository extends JpaRepository<Push, Long> {

    List<Push> findAllByPushTypeOrderByIdDesc(PushType pushType);
}
