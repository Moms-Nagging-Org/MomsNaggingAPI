package com.jasik.momsnaggingapi.domain.appVersion.repository;

import com.jasik.momsnaggingapi.domain.appVersion.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {
}
