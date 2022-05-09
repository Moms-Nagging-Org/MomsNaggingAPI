package com.jasik.momsnaggingapi.domain.grade.controller;

import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@Tag(name = "Grade API !!!", description = "상장 API")
public class GradeController {

    private final GradeService gradeService;

    @GetMapping("/week")
    @Operation(summary = "주간평가 조회", description = "해당 년도, 주차의 주간평가를 조회합니다.")
    public ResponseEntity<Grade.GradeResponse> getGrade(
        @Schema(description = "조회할 주간평가 년도", example = "2022", required = true)
        @Parameter(name = "createdYear", in = ParameterIn.QUERY)
        @PathVariable int createdYear,
        @Schema(description = "조회할 주간평가 주차", example = "15", required = true)
        @Parameter(name = "createdWeek", in = ParameterIn.QUERY)
        @PathVariable int createdWeek
    ) {
        Grade.GradeResponse result = gradeService.getGrade(createdYear, createdWeek);

        return ResponseEntity.ok().body(result);
    }
}
