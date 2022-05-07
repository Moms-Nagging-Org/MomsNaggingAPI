package com.jasik.momsnaggingapi.domain.schedule.controller;

import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.json.JsonPatch;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule API !!!", description = "할일/습관 API")
public class ScheduleController {

    private final ScheduleService scheduleService;


    @PostMapping("")
    @Operation(summary = "할일/습관 생성", description = "할일 또는 습관을 생성합니다.")
    public ResponseEntity<Schedule.ScheduleResponse> postSchedules(
            final @Valid @RequestBody Schedule.ScheduleRequest scheduleRequest
    ) {
        Schedule.ScheduleResponse result = scheduleService.postSchedule(scheduleRequest);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/schedules/" + result.getId()).toUriString());

        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping("")
    @Operation(summary = "할일/습관 목록 조회", description = "해당 일자의 모든 스케줄을 조회합니다.")
    public ResponseEntity<List<Schedule.ScheduleListResponse>> getSchedules(
            @Schema(description = "일자", example = "2022-04-16", required = true)
            @Parameter(name = "retrieveDate", description = "조회 일자", in = ParameterIn.QUERY) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate retrieveDate
    ) {
        List<Schedule.ScheduleListResponse> result = scheduleService.getSchedules(retrieveDate);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "할일/습관 조회", description = "할일 또는 습관 정보를 조회합니다.")
    public ResponseEntity<Schedule.ScheduleResponse> getSchedule(
            @Schema(description = "조회할 스케줄 ID", example = "2", required = true)
            @Parameter(name = "scheduleId", description = "조회할 스케줄 ID", in = ParameterIn.PATH)
            @PathVariable Long scheduleId
    ) {
        Schedule.ScheduleResponse result = scheduleService.getSchedule(scheduleId);

        return ResponseEntity.ok().body(result);
    }

    @PatchMapping(value = "/{scheduleId}", consumes = "application/json-patch+json")
    @Operation(summary = "할일/습관 수정", description = "할일 또는 습관 정보를 수정합니다. \n\nRFC6902 형식을 따릅니다.\n\nhttps://datatracker.ietf.org/doc/html/rfc6902 \n\n "
            + "[\n\n"
            + "    {\n\n"
            + "        \"op\": \"replace\",\n\n"
            + "        \"path\": \"/name\",\n\n"
            + "        \"value\": \"kildong\"\n\n"
            + "    },\n\n"
            + "    {\n\n"
            + "        \"op\": \"replace\",\n\n"
            + "        \"path\": \"/email\",\n\n"
            + "        \"value\": \"kildong@test.com\"\n\n"
            + "    }\n\n]"
    )
    public ResponseEntity<Schedule.ScheduleResponse> patchSchedule(
            @Schema(example = "2", required = true)
            @Parameter(name = "scheduleId", description = "수정할 스케줄 ID", in = ParameterIn.PATH)
            @PathVariable Long scheduleId,
            @RequestBody JsonPatch jsonPatch
    ) {
        Schedule.ScheduleResponse result = scheduleService.patchSchedule(scheduleId, jsonPatch);

        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "할일/습관 삭제", description = "할일 또는 습관 정보를 삭제합니다. 해당 스케줄의 이후 날짜에 대해 모두 적용됩니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    public ResponseEntity<?> deleteSchedule(
            @Schema(description = "삭제할 스케줄 ID", example = "2", required = true)
            @Parameter(name = "scheduleId", description = "삭제할 스케줄 ID", in = ParameterIn.PATH)
            @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(scheduleId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/array")
    @Operation(summary = "할일/습관 정렬", description = "할일 또는 습관을 정렬합니다.")
    public ResponseEntity<List<Schedule.ScheduleListResponse>> postSchedulesArray(
            @Schema(description = "정렬 순서에 맞게 스케줄의 ID가 저장된 배열", example = "[3, 4, 1, 2, 5]", required = true) final @RequestBody List<Long> scheduleArrayRequest
    ) {
        List<Schedule.ScheduleListResponse> result = scheduleService.postSchedulesArray(scheduleArrayRequest);

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/categories")
    @Operation(summary = "습관 추천 종류 생성", description = "습관 추천 종류를 생성합니다.")
    public ResponseEntity<Category.CategoryResponse> postCategory(
            final @Valid @RequestBody Category.CategoryRequest categoryRequest
    ) {
        Category.CategoryResponse result = scheduleService.postCategory(categoryRequest);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/categories/").toUriString());

        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping("/categories")
    @Operation(summary = "추천 습관 종류 조회", description = "추천 습관의 종류를 조회합니다.")
    public ResponseEntity<List<Category.CategoryResponse>> getScheduleCategories(
    ) {
        List<Category.CategoryResponse> result = scheduleService.getCategories();

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "추천 습관 리스트 조회", description = "추천 습관을 조회합니다.")
    public ResponseEntity<List<Schedule.CategoryListResponse>> getScheduleCategories(
            @Schema(description = "조회할 습관 종류 ID", example = "2", required = true)
            @Parameter(name = "categoryId", description = "조회할 습관 종류 ID", in = ParameterIn.PATH)
            @PathVariable Long categoryId
    ) {
        List<Schedule.CategoryListResponse> result = scheduleService.getCategorySchedules(categoryId);

        return ResponseEntity.ok().body(result);
    }
}
