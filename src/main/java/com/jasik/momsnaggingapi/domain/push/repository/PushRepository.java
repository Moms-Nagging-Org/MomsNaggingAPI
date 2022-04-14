package com.jasik.momsnaggingapi.domain.push.repository;

import com.jasik.momsnaggingapi.domain.push.Push;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushRepository extends JpaRepository<Push, Long> {
}
