package com.jasik.momsnaggingapi.domain.diary;

import com.jasik.momsnaggingapi.domain.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Diary extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String title;
    private String context;
    private LocalDate diaryDate;

    @Builder
    public Diary(Long userId, String title, String context, LocalDate diaryDate) {
        this.userId = userId;
        this.title = title;
        this.context = context;
        this.diaryDate = diaryDate;
    }

    public void updateDiary(String title, String context){
        this.title = title;
        this.context = context;
    }

    public void initUser(Long userId){
        this.userId = userId;
    }

    @Schema(description = "일기장 수정 시 요청 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiaryRequest {

        @Schema(description = "일기장 제목", defaultValue = "오랜만의 일기")
        private String title;

        @Schema(description = "일기장 내용", defaultValue = "오랜만에 일기를 써본다. 재밌었다.")
        private String context;

        @Schema(description = "일기장 날짜")
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate diaryDate;
    }

    @Schema(description = "일기장 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiaryResponse {

//        @Schema(description = "일기장 ID", defaultValue = "2")
//        private Long id;

        @Schema(description = "일기장 제목", defaultValue = "오랜만의 일기")
        private String title;

        @Schema(description = "일기장 내용", defaultValue = "오랜만에 일기를 써본다. 재밌었다.")
        private String context;

        @Schema(description = "일기장 날짜")
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate diaryDate;

        @Schema(description = "오늘 날짜 여부", defaultValue = "true", allowableValues = {"true",
            "false"})
        private boolean today;
    }
}
