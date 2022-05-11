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

    @PostMapping("/{provider}")
    @Operation(summary = "회원가입", description = "유저 정보를 회원가입 합니다. 회원가입 성공 시 토큰 전송합니다.")
    public ResponseEntity<User.AuthResponse> createUser(
            @Parameter(description = "소셜로그인 플랫폼") @PathVariable String provider,
            @Parameter(description = "플랫폼 코드") @RequestParam String code,
            @Parameter(description = "디바이스") @RequestParam String device,
            @Parameter(description = "유저 이메일") @RequestParam String email,
            @Parameter(description = "아이디") @RequestParam String id,
            @Parameter(description = "호칭") @RequestParam String nickname) {

        Optional<User> existUser = userService.existUser(code);

        if(existUser.isPresent()) {
            User.AuthResponse res = new User.AuthResponse();
            res.setToken(null);
            return ResponseEntity.status(400).body(res); // TODO: 이미 존재하는 유저일시 에러처리
        } else {
            User.CreateRequest req = new User.CreateRequest();
            req.setCode(code);
            req.setProvider(provider);
            req.setEmail(email);
            req.setDevice(device);
            req.setPersonalId(id);
            req.setNickname(nickname);

            return ResponseEntity.ok().body(userService.registerUser(req));
        }

    }

    @GetMapping("/authentication/{provider}")
    @Operation(summary = "로그인", description = "유저 정보를 전송해 로그인 합니다. 유저가 있다면 토큰을, 없다면 null 값을 전송합니다.")
    public ResponseEntity<User.AuthResponse> authenticateUser(
            @Parameter(description = "소셜로그인 플랫폼") @PathVariable String provider,
            @Parameter(description = "플랫폼 코드") @RequestParam String code) {

        Optional<User> existUser = userService.existUser(code);

        if(existUser.isPresent()) {
            User.AuthRequest req = new User.AuthRequest();
            req.setCode(code);
            req.setProvider(provider);

            return ResponseEntity.ok().body(userService.loginUser(req));
        } else {
            User.AuthResponse res = new User.AuthResponse();
            res.setToken(null);
            return ResponseEntity.ok().body(res);
        }
    }

    @GetMapping("/validate/{id}")
    @Operation(summary = "회원 검색 아이디 중복 확인", description = "id가 존재하는지 확인합니다.")
    public ResponseEntity<User.ValidateResponse> validateId(
            @Parameter(description = "아이디") @PathVariable String id) {
        User.ValidateResponse res = new User.ValidateResponse();
        res.setIsExist(userService.validateDuplicatedId(id));

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/")
    @Operation(summary = "회원 정보 가져오기", description = "유저 id로 유저를 조회합니다.")
    public ResponseEntity<User.UserResponse> getUser(@RequestParam String userId) {
        User.UserResponse response = new User.UserResponse();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/")
    @Operation(summary = "회원 정보 수정", description = "param 으로 유저 id를, body 로 수정할 정보를 보내 유저 정보를 수정합니다.")
    public ResponseEntity<User.UserResponse> updateUser(@RequestParam String userId, @RequestBody User.UpdateRequest request) {
        User.UserResponse response = new User.UserResponse();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/")
    @Operation(summary = "회원 탈퇴", description = "유저 id로 유저를 삭제합니다.")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestParam String userId) {
        Map<String, String> response = new HashMap<>();
        response.put("id", userId);
        return ResponseEntity.ok().body(response);
    }
}
