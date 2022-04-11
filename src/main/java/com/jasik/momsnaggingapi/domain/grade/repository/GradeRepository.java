package com.jasik.momsnaggingapi.domain.grade.repository;

import com.jasik.momsnaggingapi.domain.grade.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}
