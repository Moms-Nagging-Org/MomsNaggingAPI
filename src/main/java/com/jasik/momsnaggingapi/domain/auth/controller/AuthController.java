package com.jasik.momsnaggingapi.domain.auth.controller;

import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "AuthAPI ~.~", description = "로그인/회원가입 API")
public class AuthController {

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
}
