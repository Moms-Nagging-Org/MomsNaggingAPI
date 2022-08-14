package com.jasik.momsnaggingapi.domain.push;

import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleType;
import com.jasik.momsnaggingapi.infra.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Push extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long naggingId;
    private LocalDate pushDate;
    private LocalTime pushTime;
    @Enumerated(EnumType.STRING)
    private PushType pushType;
    @Column(columnDefinition = "boolean default false", name = "is_mon")
    private boolean mon;
    @Column(columnDefinition = "boolean default false", name = "is_tue")
    private boolean tue;
    @Column(columnDefinition = "boolean default false", name = "is_wed")
    private boolean wed;
    @Column(columnDefinition = "boolean default false", name = "is_thu")
    private boolean thu;
    @Column(columnDefinition = "boolean default false", name = "is_fri")
    private boolean fri;
    @Column(columnDefinition = "boolean default false", name = "is_sat")
    private boolean sat;
    @Column(columnDefinition = "boolean default false", name = "is_sun")
    private boolean sun;

    public Push(Long id, Long naggingId, LocalDate pushDate, LocalTime pushTime, PushType pushType,
        boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun) {
        this.id = id;
        this.naggingId = naggingId;
        this.pushDate = pushDate;
        this.pushTime = pushTime;
        this.pushType = pushType;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    public enum PushType {
        REPEAT, ONE
    }

    public void initPushType() {
        if (this.pushDate == null){
            this.pushType = PushType.REPEAT;
        }
        else
            this.pushType = PushType.ONE;
    }

    public void initNagging(Long naggingId) {
        this.naggingId = naggingId;
    }

    //TODO: Nagging 도메인 분리해서 naggingId 받기
    @Schema(description = "관리자에서 Push 리스트 생성 시 요청 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PushListAdminRequest {

        @Schema(description = "푸시 일자")
        @NotBlank
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate pushDate;
        @Schema(description = "푸시 시간", defaultValue = "12:00:00")
        @DateTimeFormat(iso = ISO.TIME)
        private LocalTime pushTime;
        @Schema(description = "월요일 반복 여부", defaultValue = "false")
        private boolean mon;
        @Schema(description = "화요일 반복 여부", defaultValue = "false")
        private boolean tue;
        @Schema(description = "수요일 반복 여부", defaultValue = "false")
        private boolean wed;
        @Schema(description = "목요일 반복 여부", defaultValue = "false")
        private boolean thu;
        @Schema(description = "금요일 반복 여부", defaultValue = "false")
        private boolean fri;
        @Schema(description = "토요일 반복 여부", defaultValue = "false")
        private boolean sat;
        @Schema(description = "일요일 반복 여부", defaultValue = "false")
        private boolean sun;
        @Schema(description = "푸시 제목", defaultValue = "불러보자")
        private String title;
        @Schema(description = "다정한 엄마 잔소리", defaultValue = "딸~")
        private String level1;
        @Schema(description = "냉정한 엄마 잔소리", defaultValue = "딸.")
        private String level2;
        @Schema(description = "화가 많은 엄마 잔소리", defaultValue = "딸!")
        private String level3;
    }
    @Schema(description = "관리자에서 Push 리스트 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PushListAdminResponse {

        @Schema(description = "푸시 ID", defaultValue = "9003")
        private Long id;
        @Schema(description = "푸시 일자")
        @NotBlank
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate pushDate;
        @Schema(description = "푸시 시간", defaultValue = "12:00:00")
        @DateTimeFormat(iso = ISO.TIME)
        private LocalTime pushTime;
        @Enumerated(EnumType.STRING)
        private PushType pushType;
        @Schema(description = "월요일 반복 여부", defaultValue = "false")
        private boolean mon;
        @Schema(description = "화요일 반복 여부", defaultValue = "false")
        private boolean tue;
        @Schema(description = "수요일 반복 여부", defaultValue = "false")
        private boolean wed;
        @Schema(description = "목요일 반복 여부", defaultValue = "false")
        private boolean thu;
        @Schema(description = "금요일 반복 여부", defaultValue = "false")
        private boolean fri;
        @Schema(description = "토요일 반복 여부", defaultValue = "false")
        private boolean sat;
        @Schema(description = "일요일 반복 여부", defaultValue = "false")
        private boolean sun;
        @Schema(description = "푸시 제목", defaultValue = "불러보자")
        private String title;
        @Schema(description = "다정한 엄마 잔소리", defaultValue = "딸~")
        private String level1;
        @Schema(description = "냉정한 엄마 잔소리", defaultValue = "딸.")
        private String level2;
        @Schema(description = "화가 많은 엄마 잔소리", defaultValue = "딸!")
        private String level3;
    }

}
