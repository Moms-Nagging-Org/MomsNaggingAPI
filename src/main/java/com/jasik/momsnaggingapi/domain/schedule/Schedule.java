package com.jasik.momsnaggingapi.domain.schedule;

import com.jasik.momsnaggingapi.domain.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import jdk.vm.ci.meta.Local;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;

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

    @Builder
    public Schedule(Long userId, Long originalId, Long categoryId, int seqNumber, int goalCount,
        int doneCount, String scheduleName, String scheduleTime, LocalDate scheduleDate,
        LocalTime alarmTime, boolean done,
//                    LocalDateTime routineEndDate,
        boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun) {
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
    }

    public void initOriginalId() {
        originalId = id;
    }

    public void initScheduleDate(LocalDate nextDate) { scheduleDate = nextDate; }

    public boolean plusDoneCount() {
        doneCount += 1;
        return doneCount >= goalCount;
    }

    public void initNextSchedule(){
        id = null;
        scheduleDate = scheduleDate.plusDays(1);
        done = false;
    }
    // TODO: ModelMapper에서 DTO로 넘겨주고 싶음.
//    public ScheduleType getType(){
//        if (goalCount > 0 | mon | tue | wed | thu | fri | sat | sun){
//            return ScheduleType.ROUTINE;
//        }else{
//            return ScheduleType.TODO;
//        }
//    }

    // getRepeatDays() -> objectMapper.convertValue() 에서 컬럼으로 인식하고 변환해버림.. -.-
    public boolean[] calculateRepeatDays() {
        boolean[] dayArray = {mon, tue, wed, thu, fri, sat, sun};

        return dayArray;
    }

    public enum ScheduleType {
        TODO, ROUTINE
    }

    @Schema(description = "스케줄 생성 시 요청 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @Schema(description = "사용자 ID", defaultValue = "1")
        @NotNull
        private Long userId;

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
        private LocalDate scheduleDate;

        @Schema(description = "스케줄 알람 시간")
        @DateTimeFormat(pattern = "HH:mm:ss")
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

        @Schema(description = "스케줄 유형(할일/습관)", defaultValue = "TODO")
        private ScheduleType scheduleType;
    }

    //    @Schema(description = "단일 스케줄 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private Long id;
        private Long userId;
        private int goalCount;
        private int doneCount;
        private String scheduleName;
        private String scheduleTime;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate scheduleDate;
        @DateTimeFormat(pattern = "HH:mm:ss")
        private LocalTime alarmTime;
        private boolean done;
        private boolean mon;
        private boolean tue;
        private boolean wed;
        private boolean thu;
        private boolean fri;
        private boolean sat;
        private boolean sun;
        private ScheduleType scheduleType;
    }

    //    @Schema(description = "스케줄 리스트 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListResponse {

        private Long id;
        private int seqNumber;
        private String scheduleName;
        private String scheduleTime;
        private boolean isDone;
        private ScheduleType scheduleType;

    }

    //    @Schema(description = "추천 스케줄 리스트 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryResponse {

        private Long id;
        private String scheduleName;
    }
}
