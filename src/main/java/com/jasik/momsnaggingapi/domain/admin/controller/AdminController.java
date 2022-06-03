package com.jasik.momsnaggingapi.domain.admin.controller;

import com.jasik.momsnaggingapi.domain.admin.service.AdminService;
import com.jasik.momsnaggingapi.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/users")
    @Operation(summary = "회원 정보 전체 가져오기", description = "관리자에서 보여줄 유저 리스트를 조회합니다.")
    public ResponseEntity<List<User.AdminResponse>> getAllUsers() {
        List<User.AdminResponse> response = adminService.getUsers();
        return ResponseEntity.ok().body(response);
    }
}
