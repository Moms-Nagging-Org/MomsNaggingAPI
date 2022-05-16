package com.jasik.momsnaggingapi.domain.grade.controller;

import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.Grade.GradesOfMonthResponse;
import com.jasik.momsnaggingapi.domain.grade.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

    @GetMapping("/lastWeek")
    @Operation(summary = "직전 주의 주간평가 조회", description = ""
        + "<페이지>\n\n"
        + "홈\n\n"
        + "<설명>\n\n"
        + "직전 주차의 주간평가를 조회합니다. \n\n"
        + "새롭게 달성한 상장 달성도를 함께 반환합니다.\n\n"
        + "<프론트 로직>\n\n"
        + "로그인 시 마지막 평가 주차 조회(16주차) -> 프론트에서 오늘의 주차 계산(18주차) -> 오늘 주차(18주차) - 1 != 마지막 평가 주차(16주차) -> 주간 평가 요청")
    public ResponseEntity<Grade.GradeResponse> getGradeOfLastWeek() {
        return ResponseEntity.ok().body(gradeService.getGradeOfLastWeek());
    }
    @GetMapping("/monthly")
    @Operation(summary = "월간 주간평가 조회", description = ""
        + "<페이지>\n\n"
        + "성적표 -> 통계 -> 특정 월 선택\n\n"
        + "<설명>\n\n"
        + "해당 년도, 월의 모든 주간평가를 조회합니다.")
    public ResponseEntity<List<GradesOfMonthResponse>> getWeeklyGradesOfMonth(
        @Schema(example = "2022", required = true) @Parameter(name = "retrieveYear", description = "조회할 년도", in = ParameterIn.QUERY) @RequestParam @Min(2022) @Max(2122) int retrieveYear,
        @Schema(example = "5", required = true) @Parameter(name = "retrieveMonth", description = "조회할 월", in = ParameterIn.QUERY) @RequestParam @Min(1) @Max(12) int retrieveMonth) {

        return ResponseEntity.ok().body(gradeService.getWeeklyGradesOfMonth(retrieveYear, retrieveMonth));
    }
    // TODO: Get-Me 엔드포인트에 포함시킬 데이터
    @GetMapping("/awards")
    @Operation(summary = "상장 등급 조회", description = ""
        + "<페이지>\n\n"
        + "성적표 -> 상장\n\n"
        + "<설명>\n\n"
        + "사용자가 달성한 상장 등급을 조회합니다.")
    public ResponseEntity<Grade.AwardResponse> getAwards() {
        return ResponseEntity.ok().body(gradeService.getAwards());
    }

    @GetMapping("/calendar")
    @Operation(summary = "월간 달력 성적표 조회", description = ""
        + "<페이지>\n\n"
        + "성적표 -> 달력 -> 특정 월 선택\n\n"
        + "<설명>\n\n"
        + "해당 년도, 월의 모든 일별 달성도를 조회합니다.")
    public ResponseEntity<List<Grade.Performance>> getDailyGradesOfMonth(
        @Schema(example = "2022", required = true) @Parameter(name = "retrieveYear", description = "조회할 년도", in = ParameterIn.QUERY) @RequestParam @Min(2022) @Max(2122) int retrieveYear,
        @Schema(example = "05", required = true) @Parameter(name = "retrieveMonth", description = "조회할 월", in = ParameterIn.QUERY) @RequestParam @Min(1) @Max(12) int retrieveMonth) {
        return ResponseEntity.ok().body(gradeService.getDailyGradesOfMonth(retrieveYear, retrieveMonth));
    }

    @GetMapping("/statistics")
    @Operation(summary = "성적표 통계 조회", description = ""
        + "<페이지>\n\n"
        + "성적표 -> 통계\n\n"
        + "<설명>\n\n"
        + "통계 수치를 조회합니다.")
    public ResponseEntity<Grade.StatisticsResponse> getStatistics(){
        return ResponseEntity.ok().body(gradeService.getStatistics());
    }
}
