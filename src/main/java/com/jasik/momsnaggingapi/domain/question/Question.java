package com.jasik.momsnaggingapi.domain.question;

import com.jasik.momsnaggingapi.infra.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String context;

//    @Column(columnDefinition = "TEXT")
//    private String answer;

    @Column(columnDefinition = "boolean default true")
    private boolean isQ;

    public void initUserId(Long userId) {
        this.userId = userId;
    }

    @Builder
    public Question(
        boolean isQ,
//        String answer,
        Long userId,
        String title,
        String context
    ) {
        this.userId = userId;
        this.title = title;
        this.context = context;
//        this.answer = answer;
        this.isQ = isQ;
    }

    @Schema(description = "문의 등록 시 요청 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionRequest {

        @Schema(description = "문의 제목", defaultValue = "문의있습니다")
        @NotBlank
        private String title;

        @Schema(description = "문의 내용", defaultValue = "있었는데 없습니다.")
        @NotBlank
        private String context;
    }

    @Schema(description = "문의 조회 시 응답 클래스")
    @Getter
    @Setter @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionResponse {

        @Schema(description = "문의사항 id", defaultValue = "1")
        private Long id;

        @Schema(description = "문의 제목", defaultValue = "문의있습니다")
        private String title;

        @Schema(description = "문의 내용", defaultValue = "있었는데 없습니다.")
        private String context;

        @Schema(description = "사용자 id", defaultValue = "2")
        private Long userId;

        @Schema(description = "문의 작성 일시", defaultValue = "2022-05-08 12:00:00")
        @DateTimeFormat(iso = ISO.DATE_TIME)
        private LocalDateTime createdAt;

    }

    @Schema(description = "탈퇴사유 요청 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignOutReasonRequest {
        @Schema(description = "탈퇴사유")
        @NotBlank
        private String title;

        @Schema(description = "기타")
        private String context;

    }

    @Schema(description = "탈퇴사유 조회 응답 클래스")
    @Getter @Builder
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignOutReasonResponse {
        @Schema(description = "문의사항 id")
        private Long id;

        @Schema(description = "탈퇴사유")
        private String title;

        @Schema(description = "기타")
        private String context;

        @Schema(description = "탈퇴 일시")
        private LocalDateTime createdAt;
    }
}
