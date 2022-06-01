package com.jasik.momsnaggingapi.domain.question.service;

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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Question.QuestionResponse postQuestion(Long userId, Question.QuestionRequest dto) {

        Question question = modelMapper.map(dto, Question.class);
        question.initUserId(userId);
        question = questionRepository.save(question);

        return modelMapper.map(question, Question.QuestionResponse.class);
    }

}
