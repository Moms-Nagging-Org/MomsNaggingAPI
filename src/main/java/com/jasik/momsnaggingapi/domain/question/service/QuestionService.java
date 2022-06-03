package com.jasik.momsnaggingapi.domain.question.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.diary.Diary;
import com.jasik.momsnaggingapi.domain.diary.Diary.DiaryResponse;
import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.question.Question.QuestionResponse;
import com.jasik.momsnaggingapi.domain.question.repository.QuestionRepository;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jasik.momsnaggingapi.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    public List<Question.QuestionResponse> findAllQuestions() {
        List<Question> questions = questionRepository.findAllByIsQ(true);
        return questions.stream().map(p -> modelMapper.map(p, Question.QuestionResponse.class)).collect(Collectors.toList());
    }

    @Transactional
    public List<Question.SignOutReasonResponse> findAllSignOutReasons() {
        List<Question> reasons = questionRepository.findAllByIsQ(false);
        return reasons.stream().map(p -> modelMapper.map(p, Question.SignOutReasonResponse.class)).collect(Collectors.toList());
    }
}
