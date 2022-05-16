package com.jasik.momsnaggingapi.domain.schedule;

import com.jasik.momsnaggingapi.infra.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Schedule extends BaseTime {


    @Id
    @GenericGenerator(name = "SequenceGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "hibernate_sequence"),
        @Parameter(name = "optimizer", value = "pooled"),
        @Parameter(name = "initial_value", value = "1"),
        @Parameter(name = "increment_size", value = "500")})
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGenerator")
    private Long id;

    private Long userId;

    private Long originalId;

    private Long categoryId;

    private Long naggingId;

    @Column(columnDefinition = "int default 0")
    private int seqNumber;

    @Column(columnDefinition = "int default 0")
    private int goalCount;

    @Column(columnDefinition = "int default 0")
    private int doneCount;

    @Column(nullable = false)
    private String scheduleName;

    private String scheduleTime;

    @Column(nullable = false)
    private LocalDate scheduleDate;

    private LocalTime alarmTime;
//    private LocalDateTime routineEndDate;

    @Column(columnDefinition = "boolean default false", name = "is_done")
    private boolean done;
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

    // TODO: Enumerated -> Converter 사용
    @Schema(description = "스케줄 유형(할일/습관)", defaultValue = "todo")
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    public void initOriginalId() {
        this.originalId = this.id;
    }

    public void initScheduleDate(LocalDate nextDate) {
        this.scheduleDate = nextDate;
    }
    public void initScheduleTypeAndUserId(Long userId){
        if (this.goalCount > 0 | this.mon | this.tue | this.wed | this.thu | this.fri | this.sat | this.sun){
            this.scheduleType = ScheduleType.ROUTINE;
        }else{
            this.scheduleType = ScheduleType.TODO;
        }
        this.userId = userId;
    }
    public void initGoalCount() {
        this.goalCount = 0;
    }
    // TODO: getter 안먹히는 컬럼들 -> _ 와 연관이 있는 듯
    public boolean getDone() {
        return this.done;
    }
    public boolean plusDoneCount() {
        this.doneCount += 1;
        return this.doneCount >= this.goalCount;
    }

    public void initNextSchedule() {
        this.id = null;
        this.scheduleDate = this.scheduleDate.plusDays(1);
        this.done = false;
    }

    // getRepeatDays() -> objectMapper.convertValue() 에서 컬럼으로 인식하고 변환해버림.. -.-
    public boolean[] calculateRepeatDays() {
        boolean[] dayArray = {mon, tue, wed, thu, fri, sat, sun};

        return dayArray;
    }

    public enum ScheduleType {
        TODO, ROUTINE
    }

    @Builder
    public Schedule(Long userId, Long originalId, Long categoryId, int seqNumber, int goalCount,
        int doneCount, String scheduleName, String scheduleTime, LocalDate scheduleDate,
        LocalTime alarmTime, boolean done,
//                    LocalDateTime routineEndDate,
        boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun,
        long naggingId) {
        this.userId = userId;
        this.originalId = originalId;
        this.categoryId = categoryId;
        this.seqNumber = seqNumber;
        this.scheduleName = scheduleName;   // 수정가능 -> 이후 전부 변경 -> update all -> index 유지
        this.scheduleTime = scheduleTime;   // 수정가능 -> 이후 전부 변경 -> update all -> index 유지
        this.scheduleDate = scheduleDate;   // 수정가능 -> 타겟만 변경 -> update
        this.alarmTime = alarmTime;   // 수정가능 -> 이후 전부 변경 -> update all -> index 유지
        this.done = done;
        this.goalCount = goalCount;   // 수정가능 ->-> 보류
        this.doneCount = doneCount;
        this.mon = mon;   // 수정가능 -> 이후 전부 변경 -> delete -> create
        this.tue = tue;   // 수정가능 -> 이후 전부 변경 -> delete -> create
        this.wed = wed;   // 수정가능 -> 이후 전부 변경 -> delete -> create
        this.thu = thu;   // 수정가능 -> 이후 전부 변경 -> delete -> create
        this.fri = fri;   // 수정가능 -> 이후 전부 변경 -> delete -> create
        this.sat = sat;   // 수정가능 -> 이후 전부 변경 -> delete -> create
        this.sun = sun;   // 수정가능 -> 이후 전부 변경 -> delete -> create
        this.naggingId = naggingId;
    }

    @Schema(description = "스케줄 생성 시 요청 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScheduleRequest {

        @Schema(description = "스케줄 이름", defaultValue = "술 마시기")
        @NotNull
        private String scheduleName;

        @Schema(description = "n회 습관일 경우 목표 횟수", defaultValue = "0")
        private int goalCount;

        @Schema(description = "스케줄 수행 시간", defaultValue = "아무때나")
        private String scheduleTime;

        @Schema(description = "스케줄 수행 일자")
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate scheduleDate;

        @Schema(description = "스케줄 알람 시간")
        @DateTimeFormat(iso = ISO.TIME)
//        @JsonFormat(shape = Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
        private LocalTime alarmTime;

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
    }

    @Schema(description = "단일 스케줄 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScheduleResponse {

        @Schema(description = "스케줄 ID", defaultValue = "2")
        private Long id;
        @Schema(description = "사용자 DB ID", defaultValue = "1")
        private Long userId;
        @Schema(description = "n회 습관의 수행 목표 수", defaultValue = "0")
        private int goalCount;
        @Schema(description = "n회 습관의 수행 완료 수", defaultValue = "0")
        private int doneCount;
        @Schema(description = "스케줄 이름", defaultValue = "술 마시기")
        private String scheduleName;
        @Schema(description = "스케줄 수행 시간", defaultValue = "아무때나")
        private String scheduleTime;
        @Schema(description = "스케줄 수행 일자", defaultValue = "2022-05-08")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate scheduleDate;
        @Schema(description = "스케줄 알람 시간")
        @DateTimeFormat(iso = ISO.TIME)
        private LocalTime alarmTime;
        @Schema(description = "스케줄 수행 여부", defaultValue = "false")
        private boolean done;
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
        @Schema(description = "스케줄 유형(할일/습관)", defaultValue = "todo")
        private ScheduleType scheduleType;
    }

    @Schema(description = "스케줄 리스트 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScheduleListResponse {

        @Schema(description = "스케줄 ID", defaultValue = "2")
        private Long id;
        @Schema(description = "스케줄 정렬 순서", defaultValue = "0")
        private int seqNumber;
        @Schema(description = "스케줄 이름", defaultValue = "술 마시기")
        private String scheduleName;
        @Schema(description = "스케줄 수행 시간", defaultValue = "아무때나")
        private String scheduleTime;
        @Schema(description = "수행 완료 여부", defaultValue = "false", allowableValues = {"true",
            "false"})
        private boolean isDone;
        @Schema(description = "스케줄 유형(할일/습관)", defaultValue = "todo")
        private ScheduleType scheduleType;

    }

    @Schema(description = "추천 스케줄 리스트 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryListResponse {

        @Schema(description = "스케줄 ID", defaultValue = "2")
        private Long id;
        @Schema(description = "스케줄 이름", defaultValue = "술 마시기")
        private String scheduleName;
    }
}
