package com.jasik.momsnaggingapi.domain.schedule.controller;

import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ArrayListRequest;
import com.jasik.momsnaggingapi.domain.schedule.service.ScheduleService;
import com.jasik.momsnaggingapi.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonPatch;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule API !!!", description = "할일/습관 API")
public class ScheduleController {

    private final ScheduleService scheduleService;


    @PostMapping("")
    @Operation(summary = "할일/습관 생성", description = ""
        + "<페이지>\n\n"
        + "홈 → 추가하기 → 습관 추가 → 완료\n\n"
        + "홈 → 할일 미룸 → 다시 알림 팝업 → 네(내일 날짜로 신규 생성)\n\n"
        + "<설명>\n\n"
        + "할일 또는 습관을 생성합니다.")
    public ResponseEntity<Schedule.ScheduleResponse> postSchedules(
        @AuthenticationPrincipal User user,
        final @Valid @RequestBody Schedule.ScheduleRequest scheduleRequest
    ) {
        Schedule.ScheduleResponse result = scheduleService.postSchedule(user.getId(),
            scheduleRequest);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/v1/schedules/" + result.getId()).toUriString());

        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping("")
    @Operation(summary = "할일/습관 목록 조회", description = ""
        + "<페이지>\n\n"
        + "홈\n\n"
        + "홈 → 성적표 → 달력 → 특정 일 선택\n\n"
        + "<설명>\n\n"
        + "해당 일자의 모든 스케줄을 조회합니다.\n\n"
        + "TODO 스케줄의 done 컬럼 값이 null 인 경우 '미룸' 상태입니다.")
    public ResponseEntity<List<Schedule.ScheduleListResponse>> getSchedules(
        @AuthenticationPrincipal User user,
        @Schema(description = "일자", example = "2022-04-16", required = true)
        @Parameter(name = "retrieveDate", description = "조회 일자", in = ParameterIn.QUERY) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate retrieveDate
    ) {
        List<Schedule.ScheduleListResponse> result = scheduleService.getSchedules(user.getId(), retrieveDate);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "할일/습관 상세 조회", description = ""
        + "<페이지>\n\n"
        + "홈 → 추가하기 → 습관 추가 → 추천 습관 카테고리 선택 → 습관 선택\n\n"
        + "홈 → 할일 미룸 → 다시 알림 팝업 →  아니오\n\n"
        + "홈 → 할일/습관 선택\n\n"
        + "<설명>\n\n"
        + "할일 또는 습관 정보를 조회합니다.")
    public ResponseEntity<Schedule.ScheduleResponse> getSchedule(
        @Schema(description = "조회할 스케줄 ID", example = "2", required = true)
        @Parameter(name = "scheduleId", description = "조회할 스케줄 ID", in = ParameterIn.PATH)
        @PathVariable Long scheduleId
    ) {
        Schedule.ScheduleResponse result = scheduleService.getSchedule(scheduleId);

        return ResponseEntity.ok().body(result);
    }

    // TODO: Request DTO로 만들기
    @PatchMapping(value = "/{scheduleId}", consumes = "application/json-patch+json")
    @Operation(summary = "할일/습관 수정", description = ""
        + "<페이지>\n\n"
        + "홈 → 완료(done = true)\n\n"
        + "홈 → 할일 미룸(done = null)\n\n"
        + "홈 → 할일/습관 선택 → 완료\n\n"
        + "<설명>\n\n"
        + "할일 또는 습관 정보를 수정합니다. \n\n"
        + "RFC6902 형식을 따릅니다.(https://datatracker.ietf.org/doc/html/rfc6902)\n\n "
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
        + "    }\n\n"
        + "]\n\n"
    )
    public ResponseEntity<Schedule.ScheduleResponse> patchSchedule(
        @AuthenticationPrincipal User user,
        @Schema(example = "2", required = true)
        @Parameter(name = "scheduleId", description = "수정할 스케줄 ID", in = ParameterIn.PATH)
        @PathVariable Long scheduleId,
        @RequestBody JsonPatch jsonPatch
    ) {
        Schedule.ScheduleResponse result = scheduleService.patchSchedule(user.getId(), scheduleId, jsonPatch);

        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "할일/습관 삭제", description = ""
        + "<페이지>\n\n"
        + "홈 → 삭제\n\n"
        + "<설명>\n\n"
        + "할일 또는 습관 정보를 삭제합니다. \n\n"
        + "해당 스케줄 및 이후 날짜의 스케줄이 삭제됩니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    public ResponseEntity<?> deleteSchedule(
        @AuthenticationPrincipal User user,
        @Schema(description = "삭제할 스케줄 ID", example = "2", required = true)
        @Parameter(name = "scheduleId", description = "삭제할 스케줄 ID", in = ParameterIn.PATH)
        @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(user.getId(), scheduleId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/array")
    @Operation(summary = "습관 정렬", description = ""
        + "<페이지>\n\n"
        + "홈 → 정렬 → 저장\n\n"
        + "<설명>\n\n"
        + "습관을 정렬합니다.\n\n"
        + "순서 스위칭 Object 리스트로 요청합니다.\n\n"
        + "리스트는 스위칭 시간 순으로 구성합니다.\n\n"
        + "스위칭 object는 스케줄의 originalId 컬럼으로 요청합니다.\n\n"
        + "스케줄의 originalId는 GET - /api/v1/schedules 응답에 포함되어 있습니다.")
    public ResponseEntity<?> postSchedulesArray(
        @AuthenticationPrincipal User user,
        @Schema(description = "스위칭하는 스케줄 ID 세트가 저장된 배열", required = true) final @RequestBody ArrayList<ArrayListRequest> scheduleArrayRequest
    ) {
        scheduleService.postSchedulesArray(user.getId(), scheduleArrayRequest);

        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/categories")
//    @Operation(summary = "습관 추천 종류 생성", description = "습관 추천 종류를 생성합니다.")
//    public ResponseEntity<Category.CategoryResponse> postCategory(
//            final @Valid @RequestBody Category.CategoryRequest categoryRequest
//    ) {
//        Category.CategoryResponse result = scheduleService.postCategory(categoryRequest);
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/categories/").toUriString());
//
//        return ResponseEntity.created(uri).body(result);
//    }

    @GetMapping("/categories")
    @Operation(summary = "추천 습관 종류 조회", description = ""
        + "<페이지>\n\n"
        + "홈 → 추가하기 → 습관 추가\n\n"
        + "<설명>\n\n"
        + "추천 습관의 종류를 조회합니다.")
    public ResponseEntity<List<Category.CategoryResponse>> getScheduleCategories(
        @AuthenticationPrincipal User user
    ) {
        List<Category.CategoryResponse> result = scheduleService.getCategories();

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "추천 습관 리스트 조회", description = ""
        + "<페이지>\n\n"
        + "홈 → 추가하기 → 습관 추가 → 추천 습관 카테고리 선택\n\n"
        + "<설명>\n\n"
        + "추천 습관을 조회합니다.")
    public ResponseEntity<List<Schedule.CategoryListResponse>> getScheduleCategories(
        @AuthenticationPrincipal User user,
        @Schema(description = "조회할 습관 종류 ID", example = "2", required = true)
        @Parameter(name = "categoryId", description = "조회할 습관 종류 ID", in = ParameterIn.PATH)
        @PathVariable Long categoryId
    ) {
        List<Schedule.CategoryListResponse> result = scheduleService.getCategorySchedules(
            categoryId);

        return ResponseEntity.ok().body(result);
    }
}
