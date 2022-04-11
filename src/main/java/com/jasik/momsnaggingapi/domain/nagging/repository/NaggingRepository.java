package com.jasik.momsnaggingapi.domain.nagging.repository;

import com.jasik.momsnaggingapi.domain.nagging.Nagging;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaggingRepository extends JpaRepository<Nagging, Long> {
}
