package com.jasik.momsnaggingapi.domain.question.repository;

import com.jasik.momsnaggingapi.domain.question.Interface.QuestionUserInterface;
import com.jasik.momsnaggingapi.domain.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Transactional
    @Query("select q as question, u as user from Question q inner join User u " +
            "on q.userId = u.id " +
            "where (:personalId is null or u.personalId LIKE %:personalId%) and q.isQ = :isQ")
    Page<QuestionUserInterface> findAllQuestions(@Param("personalId") String personalId, @Param("isQ") boolean isQ, Pageable pageable);

    Page<Question> findAllByIsQ(boolean isQ, Pageable pageable);

    Long countByIsQ(boolean isQ);
}
