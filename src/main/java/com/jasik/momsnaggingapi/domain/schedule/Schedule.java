package com.jasik.momsnaggingapi.domain.schedule;

import com.jasik.momsnaggingapi.domain.nagging.Nagging;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.SchedulePush;
import com.jasik.momsnaggingapi.infra.common.BaseTime;
import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import com.jasik.momsnaggingapi.infra.common.exception.NotValidRoutineException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.lang.Nullable;


@Entity
@NamedNativeQuery(
    name = "findSchedulePush",
    query = "select a.schedule_name as title, "
        + "       b.firebase_token as targetToken, "
        + "       case b.nagging_level when 1 then c.level1 when 2 then c.level2 when 3 then c.level3 else c.level1 end as body "
        + "from schedule a inner join user b on a.user_id = b.id inner join nagging c on a.nagging_id = c.id\n"
        + "where a.schedule_date = :scheduleDate "
        + "and a.alarm_time = :alarmTime",
    resultSetMapping = "SchedulePush")
@SqlResultSetMapping(name = "SchedulePush", classes = @ConstructorResult(targetClass = SchedulePush.class, columns = {
    @ColumnResult(name = "title", type = String.class),
    @ColumnResult(name = "targetToken", type = String.class),
    @ColumnResult(name = "body", type = String.class)}))
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
    private int goalCount;

    @Column(columnDefinition = "int default 0")
    private int doneCount;

    @Column(nullable = false)
    private String scheduleName;

    private String scheduleTime;

//    @Column(nullable = false)
    private LocalDate scheduleDate;

    private LocalTime alarmTime;
//    private LocalDateTime routineEndDate;

    @Column(columnDefinition = "int default 0")
    private int status;
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
    public boolean plusDoneCount() {
        this.doneCount += 1;
        return this.doneCount >= this.goalCount;
    }

    @Builder
    public Schedule(Long userId, Long originalId, Long categoryId, int goalCount,
        int doneCount, String scheduleName, String scheduleTime, LocalDate scheduleDate,
        LocalTime alarmTime, int status,
        boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun,
        long naggingId) {
        this.userId = userId;
        this.originalId = originalId;
        this.categoryId = categoryId;
        this.scheduleName = scheduleName;   // 수정가능 -> 이후 전부 변경 -> update all -> index 유지
        this.scheduleTime = scheduleTime;   // 수정가능 -> 이후 전부 변경 -> update all -> index 유지
        this.scheduleDate = scheduleDate;   // 수정가능 -> 타겟만 변경 -> update
        this.alarmTime = alarmTime;   // 수정가능 -> 이후 전부 변경 -> update all -> index 유지
        this.status = status;
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

    public void initNextSchedule() {
        this.scheduleDate = this.scheduleDate.plusDays(1);
        this.status = 0;
    }

    // getRepeatDays() -> objectMapper.convertValue() 에서 컬럼으로 인식하고 변환해버림.. -.-
    public boolean[] calculateRepeatDays() {
        boolean[] dayArray = {mon, tue, wed, thu, fri, sat, sun};

        return dayArray;
    }

    public void verifyRoutine() {
        if ((this.mon | this.tue | this.wed | this.thu | this.fri | this.sat | this.sun) && (
            this.goalCount > 0)) {
            throw new NotValidRoutineException("Repeat option of routine is not valid",
                ErrorCode.ROUTINE_NOT_VALID);
        }
    }

    public enum ScheduleType {
        TODO, ROUTINE
    }

    public void minusDoneCount() {
        if (this.doneCount > 0) {
            this.doneCount -= 1;
        }
    }

    @Schema(description = "스케줄 생성 시 요청 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScheduleRequest {

        @Schema(description = "스케줄 이름", defaultValue = "술 마시기")
        @NotBlank
        private String scheduleName;

        @Schema(description = "잔소리 Id, 없는 경우 null")
        @Nullable
        private Long naggingId;

        @Schema(description = "n회 습관일 경우 목표 횟수", defaultValue = "0")
        private int goalCount;

        @Schema(description = "스케줄 수행 시간", defaultValue = "아무때나")
        private String scheduleTime;

        @Schema(description = "스케줄 수행 일자")
        @NotBlank
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate scheduleDate;

        @Schema(description = "스케줄 알람 시간", defaultValue = "12:00:00")
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
        @Schema(description = "잔소리 Id", defaultValue = "1")
        private Long naggingId;
        @Schema(description = "n회 습관의 수행 목표 수", defaultValue = "0")
        private int goalCount;
        //        @Schema(description = "n회 습관의 수행 완료 수", defaultValue = "0")
//        private int doneCount;
        @Schema(description = "스케줄 이름", defaultValue = "술 마시기")
        private String scheduleName;
        @Schema(description = "스케줄 수행 시간", defaultValue = "아무때나")
        private String scheduleTime;
        @Schema(description = "스케줄 수행 일자", defaultValue = "2022-05-08")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate scheduleDate;
        @Schema(description = "스케줄 알람 시간", defaultValue = "12:00:00")
        @DateTimeFormat(iso = ISO.TIME)
        private LocalTime alarmTime;
        @Schema(description = "스케줄 상태, 0: 미완, 1: 완료, 2: 미룸/건너뜀", defaultValue = "0")
        private int status;
//        @Schema(description = "스케줄 수행 여부", defaultValue = "false")
//        private boolean done;
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

        @Schema(description = "스케줄 ID", defaultValue = "22")
        private Long id;
        @Schema(description = "스케줄 원본 ID", defaultValue = "1")
        private Long originalId;
        @Schema(description = "n회 습관의 수행 목표 수", defaultValue = "0")
        private int goalCount;
        //        @Schema(description = "n회 습관의 수행 완료 수", defaultValue = "0")
//        private int doneCount;
//        @Schema(description = "잔소리 Id", defaultValue = "1")
//        private Long naggingId;
        @Schema(description = "스케줄 이름", defaultValue = "술 마시기")
        private String scheduleName;
        @Schema(description = "스케줄 수행 시간", defaultValue = "아무때나")
        private String scheduleTime;
        @Schema(description = "스케줄 상태, 0: 미완, 1: 완료, 2: 미룸/건너뜀", defaultValue = "0")
        private int status;
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

    @Schema(description = "관리자에서 추천 스케줄 리스트 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryListAdminResponse {

        @Schema(description = "스케줄 ID", defaultValue = "9003")
        private Long id;
        @Schema(description = "스케줄 이름", defaultValue = "침대 정리하기")
        private String scheduleName;
        @Schema(description = "다정한 엄마", defaultValue = "딸~")
        private String level1;
        @Schema(description = "냉정한 엄마", defaultValue = "딸.")
        private String level2;
        @Schema(description = "화가 많은 엄마", defaultValue = "딸!")
        private String level3;
    }

    @Schema(description = "스케줄 순서 변경 시 요청 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArrayListRequest {

        @Schema(description = "순서를 스위칭할 스케줄의 original ID")
        private Long oneOriginalId;

        @Schema(description = "순서를 스위칭할 또 다른 스케줄의 original ID")
        private Long theOtherOriginalId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SchedulePush {

        private String title;
        private String targetToken;
        private String body;
    }
}
