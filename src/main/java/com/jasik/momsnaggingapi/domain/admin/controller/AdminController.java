package com.jasik.momsnaggingapi.domain.admin.controller;

import com.jasik.momsnaggingapi.domain.admin.Admin;
import com.jasik.momsnaggingapi.domain.admin.service.AdminService;
import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "AdminAPI ~.~", description = "관리자페이지 API")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "대시보드 정보 가져오기", description = "관리자 대시보드에서 보여줄 데이터를 조회합니다." +
            "\n\n nofUsers - 총 회원 수 | nofQuestions - 문의사항 개수 " +
            "\n\n nofLv1 - 우리 집 엄친아 수 | nofLv2 - 우리 동네 엄친아 수 | nofLv3 - 지구 엄친아 수 | nofLv4 - 우주 엄친아 수")
    public ResponseEntity<Admin.DashBoardResponse> getAdmin() {
        Admin.DashBoardResponse response = new Admin.DashBoardResponse();
        Admin.ServiceData serviceData = adminService.getServiceData();
        Admin.GradeData gradeData = adminService.getGradeData();
        response.setGrade(gradeData);
        response.setService(serviceData);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/users")
    @Operation(summary = "회원 정보 전체 가져오기", description = "관리자에서 보여줄 유저 리스트를 조회합니다.")
    public ResponseEntity<List<User.AdminResponse>> getAllUsers() {
        return ResponseEntity.ok().body(adminService.getUsers());
    }

    @GetMapping("/questions")
    @Operation(summary = "문의사항 전체 가져오기", description = "관리자에서 보여줄 문의사항 리스트를 조회합니다.")
    public ResponseEntity<List<Question.QuestionResponse>> getAllQuestions() {
        return ResponseEntity.ok().body(adminService.getQuestions());
    }

    @GetMapping("/singout")
    @Operation(summary = "탈퇴사유 전체 가져오기", description = "관리자에서 보여줄 탈퇴사유 리스트를 조회합니다.")
    public ResponseEntity<List<Question.SignOutReasonResponse>> getAllSignOutReasons() {
        return ResponseEntity.ok().body(adminService.getSignOutReasons());
    }

    @GetMapping("/schedules/categories/{categoryId}")
    @Operation(summary = "추천 습관 카테고리 별 가져오기", description = "관리자에서 보여줄 카테고리 별 추천 습관 리스트를 조회합니다.")
    public ResponseEntity<List<Schedule.CategoryListAdminResponse>> getTemplateSchedulesByCategories(
            @Schema(description = "조회할 습관 종류 ID", example = "1", required = true)
            @Parameter(name = "categoryId", description = "조회할 습관 종류 ID", in = ParameterIn.PATH)
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok().body(adminService.getTemplateSchedulesByCategory(categoryId));
    }
}
