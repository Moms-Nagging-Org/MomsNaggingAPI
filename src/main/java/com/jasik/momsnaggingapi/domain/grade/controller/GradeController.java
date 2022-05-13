package com.jasik.momsnaggingapi.domain.grade.controller;

import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@Tag(name = "Grade API !!!", description = "평가 API")
public class GradeController {

    private final GradeService gradeService;

    @GetMapping("")
    @Operation(summary = "주간평가 조회", description = "해당 년도, 주차의 주간평가를 조회합니다.")
    public ResponseEntity<Grade.GradeResponse> getGrade(
        @Schema(example = "2022", required = true) @Parameter(name = "createdYear", description = "조회할 주간평가 년도", in = ParameterIn.QUERY) @RequestParam int createdYear,
        @Schema(example = "15", required = true) @Parameter(name = "createdWeek", description = "조회할 주간평가 주차", in = ParameterIn.QUERY) @RequestParam int createdWeek) {
        Grade.GradeResponse result = gradeService.getGrade(createdYear, createdWeek);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/awards")
    @Operation(summary = "상장 등급 조회", description = "상장 등급을 반환합니다.")
    public ResponseEntity<Integer> getAwards() {
        return ResponseEntity.ok().body(gradeService.getAwards());
    }

    @GetMapping("/monthly")
    @Operation(summary = "월간 달력 성적표 조회", description = "해당 년월의 성적표를 반환합니다. \n\n일별 달성률과 스케줄 리스트를 반환합니다.")
    public ResponseEntity<List<HashMap>> getMonthly(
        @Schema(example = "2022", required = true) @Parameter(name = "retrieveYear", description = "조회할 년도", in = ParameterIn.QUERY) @RequestParam int retrieveYear,
        @Schema(example = "05", required = true) @Parameter(name = "retrieveMonth", description = "조회할 월", in = ParameterIn.QUERY) @RequestParam int retrieveMonth) {
        return ResponseEntity.ok().body(gradeService.getMonthly(retrieveYear, retrieveMonth));
    }
}
