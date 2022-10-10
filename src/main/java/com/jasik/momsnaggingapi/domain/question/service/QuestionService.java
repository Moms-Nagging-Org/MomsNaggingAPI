package com.jasik.momsnaggingapi.domain.question.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jasik.momsnaggingapi.domain.question.Interface.QuestionUserInterface;
import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.question.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@JsonIgnoreProperties(value={"hibernateLazyInitializer", "handler"})
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Question.QuestionResponse postQuestion(Long userId, Question.QuestionRequest dto) {

        Question question = modelMapper.map(dto, Question.class);
        question.initUserId(userId);
        question.setQ(true);
        question = questionRepository.save(question);

        return modelMapper.map(question, Question.QuestionResponse.class);
    }

    @Transactional
    public void createSignOutReason(Long userId, Question.SignOutReasonRequest request) {
        Question signOutReason = Question.builder()
                .userId(userId)
                .title(request.getTitle())
                .context(request.getContext())
                .isQ(false)
                .build();
        questionRepository.save(signOutReason);
    }

    @Transactional
    public Long countAllQuestions() {
        return questionRepository.countByIsQ(true);
    }

    @Transactional
    public Page<QuestionUserInterface> findAllQuestions(Pageable pageable, String search) {
        return questionRepository.findAllQuestions(search, true, pageable);
    }

    @Transactional
    public Page<Question> findAllSignOutReasons(Pageable pageable) {
        return questionRepository.findAllByIsQ(false, pageable);
    }
}
