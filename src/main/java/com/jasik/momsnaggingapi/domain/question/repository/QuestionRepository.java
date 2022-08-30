package com.jasik.momsnaggingapi.domain.question.repository;

import com.jasik.momsnaggingapi.domain.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAllByIsQ(boolean isQ, Pageable pageable);

    Long countByIsQ(boolean isQ);
}
