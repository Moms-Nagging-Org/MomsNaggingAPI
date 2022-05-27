package com.jasik.momsnaggingapi.domain.user.controller;

import com.jasik.momsnaggingapi.domain.auth.jwt.JwtHeaderUtil;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "UserAPI ~.~", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    @Operation(summary = "회원 정보 가져오기", description = "유저를 조회합니다.")
    public ResponseEntity<User.UserResponse> getUser(HttpServletRequest request) {
        String token = JwtHeaderUtil.getToken(request);

        User.UserResponse response = userService.findUser(token);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("")
    @Operation(summary = "회원 정보 수정", description = "body 로 수정할 정보를 보내 유저 정보를 수정합니다.")
    public ResponseEntity<User.Response> updateUser(HttpServletRequest request, @RequestBody User.UpdateRequest user) {
        String token = JwtHeaderUtil.getToken(request);

        return ResponseEntity.ok().body(userService.editUser(token, user));
    }

    @DeleteMapping("")
    @Operation(summary = "회원 탈퇴", description = "유저를 삭제합니다.")
    public ResponseEntity<User.Response> deleteUser(HttpServletRequest request) {
        String token = JwtHeaderUtil.getToken(request);

        return ResponseEntity.ok().body(userService.removeUser(token));
    }
}
