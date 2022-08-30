package com.jasik.momsnaggingapi.domain.admin;

import com.jasik.momsnaggingapi.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

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
}
