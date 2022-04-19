package com.jasik.momsnaggingapi.domain.user.controller;

import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "UserAPI ~.~", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    @Operation()
    public void createUser() {
        return;
    }

    @GetMapping("/{userId}")
    @Operation()
    public void getUser() {
        return;
    }

    @PutMapping("/{userId}")
    @Operation()
    public void updateUser() {
        return;
    }

    @DeleteMapping("/{userId}")
    @Operation()
    public void deleteUser() {
        return;
    }
}
