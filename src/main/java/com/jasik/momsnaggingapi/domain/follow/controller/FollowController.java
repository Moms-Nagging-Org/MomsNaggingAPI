package com.jasik.momsnaggingapi.domain.follow.controller;

import com.jasik.momsnaggingapi.domain.follow.Follow.FollowResponse;
import com.jasik.momsnaggingapi.domain.follow.service.FollowService;
import com.jasik.momsnaggingapi.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@Tag(name = "Follow API !!!", description = "식구 팔로우/팔로워 API")
public class FollowController {

    private final FollowService followService;

    @GetMapping("follower")
    @Operation(summary = "팔로워 조회",
        description = ""
            + "<페이지>\n\n"
            + "홈 → 식구\n\n"
            + "홈 → 식구 → 내 식구 목록 → 팔로워\n\n"
            + "<설명>\n\n"
            + "유저를 팔로우하는 유저 목록을 조회합니다")
    public ResponseEntity<List<FollowResponse>> getFollower(
        @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok().body(followService.getFollowers(user.getId()));
    }

    @GetMapping("following")
    @Operation(summary = "팔로우 조회",
        description = ""
            + "<페이지>\n\n"
            + "홈 → 식구\n\n"
            + "홈 → 식구 → 내 식구 목록 → 팔로잉\n\n"
            + "<설명>\n\n"
            + "유저가 팔로우하는 유저 목록을 조회합니다")
    public ResponseEntity<List<FollowResponse>> getFollowings(
        @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok().body(followService.getFollowings(user.getId()));
    }
}
