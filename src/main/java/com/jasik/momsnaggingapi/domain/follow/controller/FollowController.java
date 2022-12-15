package com.jasik.momsnaggingapi.domain.follow.controller;

import com.jasik.momsnaggingapi.domain.follow.Follow.FollowResponse;
import com.jasik.momsnaggingapi.domain.follow.service.FollowService;
import com.jasik.momsnaggingapi.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    ) {
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
    ) {
        return ResponseEntity.ok().body(followService.getFollowings(user.getId()));
    }

    @PostMapping("following")
    @Operation(summary = "계정 팔로우",
        description = ""
            + "<페이지>\n\n"
            + "홈 → 식구 → 검색\n\n"
            + "홈 → 식구 → 내 식구 목록 → 팔로워\n\n"
            + "<설명>\n\n"
            + "유저를 팔로우 합니다")
    @ApiResponse(responseCode = "204", description = "추가 성공")
    public ResponseEntity<?> postFollowing(
        @AuthenticationPrincipal User user,
        @Schema(description = "유저 번호", example = "22", required = true)
        @Parameter(name = "toUserId", description = "유저 번호", in = ParameterIn.QUERY)
        @RequestParam Long toUserId
        ) {
        followService.postFollowing(user, toUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("following")
    @Operation(summary = "계정 팔로우 취소",
        description = ""
            + "<페이지>\n\n"
            + "홈 → 식구 → 내 식구 목록 → 팔로잉\n\n"
            + "홈 → 식구 → 내 식구 목록 → 식구 스케줄\n\n"
            + "<설명>\n\n"
            + "유저 팔로우를 취소합니다")
    @ApiResponse(responseCode = "204", description = "취소 성공")
    public ResponseEntity<?> deleteFollowing(
        @AuthenticationPrincipal User user,
        @Schema(description = "유저 번호", example = "22", required = true)
        @Parameter(name = "toUserId", description = "유저 번호", in = ParameterIn.QUERY)
        @RequestParam Long toUserId
    ) {
        followService.deleteFollowing(user, toUserId);
        return ResponseEntity.noContent().build();
    }
}
