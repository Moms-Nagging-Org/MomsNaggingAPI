package com.jasik.momsnaggingapi.domain.user.controller;

import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "UserAPI ~.~", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/authentication/{provider}")
    @Operation(summary = "회원가입/로그인", description = "유저 정보를 전송해 회원가입/로그인 합니다. 유저가 있다면 로그인을, 없다면 회원가입을 진행합니다.")
    public ResponseEntity<Map<String, String>> authenticateUser(
            @Parameter(description = "소셜로그인 플랫폼") @PathVariable String provider,
            @Parameter(description = "플랫폼 코드") @RequestParam String code,
            @Parameter(description = "유저 이메일(회원가입시 필수 전달)") @RequestParam(required = false) String email) {
        // TODO: email exist 확인
        Map<String, String> response = new HashMap<>();

        Optional<User> existUser = userService.existUser(code);
        if(existUser.isPresent()) {
            User.AuthRequest req = new User.AuthRequest();
            req.setCode(code);
            req.setProvider(provider);
            response.put("token", userService.loginUser(req).getToken());
        } else {
            User.CreateRequest req = new User.CreateRequest();
            req.setCode(code);
            req.setProvider(provider);
            req.setEmail(email);
            User.CreateResponse res = userService.registerUser(req);
            response.put("provider", res.getProvider());
            response.put("code", res.getCode());
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "회원 정보 가져오기", description = "유저 id로 유저를 조회합니다.")
    public ResponseEntity<User.UserResponse> getUser(@RequestParam String userId) {
        User.UserResponse response = new User.UserResponse();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "회원 정보 수정", description = "param 으로 유저 id를, body 로 수정할 정보를 보내 유저 정보를 수정합니다.")
    public ResponseEntity<User.UserResponse> updateUser(@RequestParam String userId, @RequestBody User.UpdateRequest request) {
        User.UserResponse response = new User.UserResponse();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴", description = "유저 id로 유저를 삭제합니다.")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestParam String userId) {
        Map<String, String> response = new HashMap<>();
        response.put("id", userId);
        return ResponseEntity.ok().body(response);
    }
}
