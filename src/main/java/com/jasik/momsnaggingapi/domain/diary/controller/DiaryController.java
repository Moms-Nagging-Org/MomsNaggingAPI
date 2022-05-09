package com.jasik.momsnaggingapi.domain.diary.controller;

import com.jasik.momsnaggingapi.domain.diary.Diary;
import com.jasik.momsnaggingapi.domain.diary.Diary.DiaryResponse;
import com.jasik.momsnaggingapi.domain.diary.service.DiaryService;
import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Category.CategoryResponse;
import com.jasik.momsnaggingapi.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
@Tag(name = "Diary API !!!", description = "일기장 API")
public class DiaryController {

    private final DiaryService diaryService;

    @PutMapping("")
    @Operation(summary = "일기장 수정",
        description = "해당 일자의 일기장 내용을 수정합니다.\n\n"
            + "null 값으로 보내는 컬럼은 그대로 저장됩니다.\n\n"
            + "수정하지 않은 값도 저장될 내용 그대로 컬럼 값으로 보내야합니다.")
    public ResponseEntity<DiaryResponse> putDiary(
        final @Valid @RequestBody Diary.DiaryRequest request
    ) {
        Diary.DiaryResponse result = diaryService.putDiary(request);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("")
    @Operation(
        summary = "일기장 조회",
        description = "해당 일자의 일기장 내용을 조회합니다.\n\n"
            + "today 컬럼을 통해 오늘 날짜 여부를 확인합니다.")
    public ResponseEntity<DiaryResponse> getDiary(
        @Schema(description = "일자", example = "2022-04-16", required = true)
        @Parameter(name = "retrieveDate", description = "조회 일자", in = ParameterIn.QUERY) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate retrieveDate
    ) {
        Diary.DiaryResponse result = diaryService.getDiary(retrieveDate);

        return ResponseEntity.ok().body(result);
    }
}
