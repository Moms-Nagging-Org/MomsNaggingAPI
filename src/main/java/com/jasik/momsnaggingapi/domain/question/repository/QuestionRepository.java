package com.jasik.momsnaggingapi.domain.question.repository;

import com.jasik.momsnaggingapi.domain.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
