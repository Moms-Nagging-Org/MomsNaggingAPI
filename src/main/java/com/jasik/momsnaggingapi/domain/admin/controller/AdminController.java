package com.jasik.momsnaggingapi.domain.admin.controller;

import com.jasik.momsnaggingapi.domain.admin.Admin;
import com.jasik.momsnaggingapi.domain.admin.service.AdminService;
import com.jasik.momsnaggingapi.domain.push.Push;
import com.jasik.momsnaggingapi.domain.push.Push.PushType;
import com.jasik.momsnaggingapi.domain.push.service.PushService;
import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "AdminAPI ~.~", description = "관리자페이지 API")
public class AdminController {
    private final AdminService adminService;
    private final PushService pushService;

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
    public ResponseEntity<Page<User.AdminResponse>> getAllUsers(@PageableDefault(size=10, sort="id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok().body(adminService.getUsers(pageable));
    }

    @GetMapping("/questions")
    @Operation(summary = "문의사항 전체 가져오기", description = "관리자에서 보여줄 문의사항 리스트를 조회합니다.")
    public ResponseEntity<Page<Question.QuestionResponse>> getAllQuestions(@PageableDefault(size=10, sort="id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok().body(adminService.getQuestions(pageable));
    }

    @GetMapping("/sign-out")
    @Operation(summary = "탈퇴사유 전체 가져오기", description = "관리자에서 보여줄 탈퇴사유 리스트를 조회합니다.")
    public ResponseEntity<Page<Question.SignOutReasonResponse>> getAllSignOutReasons(@PageableDefault(size=10, sort="id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok().body(adminService.getSignOutReasons(pageable));
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

    @GetMapping("/push")
    @Operation(summary = "푸시 알림 전체 가져오기", description = "관리자에서 보여줄 푸시 알림 리스트를 조회합니다.")
    public ResponseEntity<List<Push.PushListAdminResponse>> getAllPushes(
        @Parameter(name = "pushType", description = "조회할 푸시 종류, repeat: 정기성, one: 일회성")
        @RequestParam PushType pushType
        ) {
        return ResponseEntity.ok().body(pushService.getPushes(pushType));
    }
    @PostMapping("push")
    @Operation(summary = "푸시 알림 생성", description = "관리자에서 푸시 알림을 생성합니다.")
    public ResponseEntity<Push.PushListAdminResponse> postSchedules(
        final @Valid @RequestBody Push.PushListAdminRequest pushRequest
    ) {
        Push.PushListAdminResponse result = pushService.postPush(pushRequest);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/v1/admin/push/" + result.getId()).toUriString());

        return ResponseEntity.created(uri).body(result);
    }
}
