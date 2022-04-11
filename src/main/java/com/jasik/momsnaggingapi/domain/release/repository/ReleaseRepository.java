package com.jasik.momsnaggingapi.domain.release.repository;

import com.jasik.momsnaggingapi.domain.release.Release;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseRepository extends JpaRepository<Release, Long> {
}
