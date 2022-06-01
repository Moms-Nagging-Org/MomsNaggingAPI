package com.jasik.momsnaggingapi.domain.question.controller;

import com.jasik.momsnaggingapi.domain.diary.Diary;
import com.jasik.momsnaggingapi.domain.diary.Diary.DiaryResponse;
import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.question.Question.QuestionResponse;
import com.jasik.momsnaggingapi.domain.question.service.QuestionService;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/question")
@RequiredArgsConstructor
@Tag(name = "Question API !!!", description = "문의사항 API")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("")
    @Operation(summary = "문의사항 생성", description = "문의사항을 생성합니다.")
    public ResponseEntity<Question.QuestionResponse> postQuestion(
        @AuthenticationPrincipal User user,
        final @Valid @RequestBody Question.QuestionRequest request
    ) {
        Question.QuestionResponse result = questionService.postQuestion(user.getId(), request);
        URI uri = URI.create(
            ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/questions/" + result.getId()).toUriString());

        return ResponseEntity.created(uri).body(result);
    }

    // TODO : pagination 적용
//    @GetMapping("")
//    @Operation(
//        summary = "문의사항 조회",
//        description = "문의사항 리스트를 조회합니다.")
//    public ResponseEntity<List<QuestionResponse>> getQuestions() {
//        List<QuestionResponse> result = questionService.getQuestions();
//
//        return ResponseEntity.ok().body(result);
//    }

    // TODO: excel 다운로드 api

    // TODO: 문의 등록 api

}
