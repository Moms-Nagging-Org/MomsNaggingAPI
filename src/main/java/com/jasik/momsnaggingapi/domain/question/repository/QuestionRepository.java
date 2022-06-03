package com.jasik.momsnaggingapi.domain.question.repository;

import com.jasik.momsnaggingapi.domain.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByIsQ(boolean isQ);
}
