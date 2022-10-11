package com.jasik.momsnaggingapi.domain.admin;

import com.jasik.momsnaggingapi.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class Admin {

    @Schema(description = "가입자 정보 목록")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ServiceData {
        private Long nOfUsers;
        private Long nOfQuestions;
    }

    @Schema(description = "대시보드 상장 정보")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GradeData {
        private int nOfLv1;
        private int nOfLv2;
        private int nOfLv3;
        private int nOfLv4;
    }

    @Schema(description = "대시보드 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DashBoardResponse {
        private ServiceData service;
        private GradeData grade;
    }

    @Schema(description = "문의 조회 시 응답 클래스")
    @Getter @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionResponse {

        @Schema(description = "문의사항 id", defaultValue = "1")
        private Long id;

        @Schema(description = "문의 제목", defaultValue = "문의있습니다")
        private String title;

        @Schema(description = "문의 내용", defaultValue = "있었는데 없습니다.")
        private String context;

        @Schema(description = "사용자 id", defaultValue = "")
        private String userId;

        @Schema(description = "문의 작성 일시", defaultValue = "2022-05-08 12:00:00")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime createdAt;
    }
}
