package com.jasik.momsnaggingapi.domain.user.controller;

import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "UserAPI ~.~", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    @Operation(summary = "회원 정보 가져오기", description = "유저를 조회합니다.")
    public ResponseEntity<User.UserResponse> getUser(@AuthenticationPrincipal User user) {
        User.UserResponse response = userService.findUser(user.getId());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("")
    @Operation(summary = "회원 정보 수정", description = "body 로 수정할 정보를 보내 유저 정보를 수정합니다.\n\n"
            + "**전부 전송해도 되고 바뀌는것만 전송해도 됩니다**")
    public ResponseEntity<User.Response> updateUser(@AuthenticationPrincipal User user, @RequestBody User.UpdateRequest req) {
        return ResponseEntity.ok().body(userService.editUser(user.getId(), req));
    }

    @DeleteMapping("")
    @Operation(summary = "회원 탈퇴", description = "body 로 탈퇴사유를 전송하고, 해당 유저를 삭제합니다.")
    public ResponseEntity<User.Response> deleteUser(@AuthenticationPrincipal User user, @RequestBody Question.SignOutReasonRequest req) {
        return ResponseEntity.ok().body(userService.removeUser(user.getId(), req));
    }
}
