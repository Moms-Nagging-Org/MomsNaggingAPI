package com.jasik.momsnaggingapi.domain.grade;

import com.jasik.momsnaggingapi.infra.common.BaseTime;
import com.jasik.momsnaggingapi.domain.grade.Grade.Performance;
import com.jasik.momsnaggingapi.domain.grade.Grade.StatisticsResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleListResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NamedNativeQuery;

@Entity
@NamedNativeQuery(name = "findPerformanceOfPeriod", query =
    "select IF(a.performance is null, null, a.performance) as avg, b.date "
        + "from "
        + "(select (count(if(status=1,1,null))/count(*))*100 as performance, schedule_date "
        + "from schedule "
        + "where user_id = :userId "
        + "and schedule_date >= :startDate "
        + "and schedule_date <= :endDate "
        + "group by schedule_date) a "
        + "right outer join ( "
        + "    SELECT "
        + "        DATE_FORMAT(DATE_ADD(:startDate, INTERVAL seq - 1 DAY), '%Y-%m-%d') AS date "
        + "    FROM (SELECT @num \\:= @num + 1 AS seq "
        + "          FROM information_schema.tables a "
        + "             , information_schema.tables b "
        + "             , (SELECT @num \\:= 0) c "
        + "         ) T "
        + "    WHERE seq <=  DATEDIFF(:endDate, :startDate) + 1 "
        + ") b on a.schedule_date = b.date;",
    resultSetMapping = "Performance")
@SqlResultSetMapping(name = "Performance", classes = @ConstructorResult(targetClass = Performance.class, columns = {
    @ColumnResult(name = "avg", type = Integer.class),
    @ColumnResult(name = "date", type = LocalDate.class)
}))
@NamedNativeQuery(name = "findStatisticsResponse", query =
    "select count(if(a.performance=100.0,1,NULL)) as fullDoneCount, "
        + "count(if(a.performance<100 and a.performance>0,1,NULL)) as partialDoneCount, "
        + "b.todo_count as todoDoneCount, "
        + "b.routine_count as routineDoneCount, "
        + "c.writen_cnt as diaryCount, "
        + "d.together_count as togetherCount, "
        + "e.avg_performance as performanceAvg "
        + "from("
        + "        select (count(if(status=1, 1, NULL))/count(*))*100 as performance"
        + "        from schedule"
        + "        where user_id = :userId"
        + "        group by schedule_date"
        + "        ) a,"
        + "    ("
        + "        select count(if(schedule_type='todo',1,Null)) as todo_count, count(if(schedule_type='routine',1,Null)) as routine_count"
        + "        from schedule"
        + "        where user_id = :userId"
        + "          and status = 1"
        + "    ) b,"
        + "    ("
        + "        select count(*) as writen_cnt"
        + "        from diary"
        + "        where user_id = :userId"
        + "          and title is not null"
        + "          and context is not null"
        + "    ) c,"
        + "    ("
        + "        select TIMESTAMPDIFF(DAY, created_at, CURDATE()) + 1 as together_count"
        + "        from user"
        + "        where id = :userId"
        + "    ) d,"
        + "    ("
        + "        select ROUND(count(if(status=1,1,NULL)) / count(*) * 100) as avg_performance"
        + "        from schedule"
        + "        where user_id = :userId"
        + "          and schedule_date <= :endDate "
        + "    ) e",
    resultSetMapping = "StatisticsResponse")
@SqlResultSetMapping(name = "StatisticsResponse", classes = @ConstructorResult(targetClass = StatisticsResponse.class, columns = {
    @ColumnResult(name = "fullDoneCount", type = Long.class),
    @ColumnResult(name = "partialDoneCount", type = Long.class),
    @ColumnResult(name = "performanceAvg", type = Integer.class),
    @ColumnResult(name = "todoDoneCount", type = Long.class),
    @ColumnResult(name = "routineDoneCount", type = Long.class),
    @ColumnResult(name = "diaryCount", type = Long.class),
    @ColumnResult(name = "togetherCount", type = Integer.class)
}))
@Getter
@NoArgsConstructor
public class Grade extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;

    private int gradeLevel;
    private int createdYear;
    private int createdWeek;

    @Builder
    public Grade(Long userId, int gradeLevel, int createdYear, int createdWeek) {
        this.userId = userId;
        this.gradeLevel = gradeLevel;
        this.createdYear = createdYear;
        this.createdWeek = createdWeek;
    }

    @Schema(description = "주간평가 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GradeResponse {

        @Schema(description = "평가 단계\n\n1:수, 2:우, 3:미, 4:양, 5:가", defaultValue = "3", allowableValues = {"1", "2", "3",
            "4", "5"})
        private int gradeLevel;
        @Schema(description = "년도", defaultValue = "2022")
        private int createdYear;
        @Schema(description = "주차\n\n(월요일 ~ 일요일)", defaultValue = "15")
        private int createdWeek;
        @Schema(description = "새롭게 달성한 상장의 단계\n\n0인 경우 새로 달성한 상장 없음\n\n주간평가 생성 시에만 사용되는 컬럼", defaultValue = "0")
        private int awardLevel;
        @Schema(description = "주간 평가 신규 생성 여부", defaultValue = "True")
        private boolean newGrade;
    }
    @Schema(description = "월간 달력 성적표 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class calendarResponse {

        @Schema(description = "날짜")
        private LocalDate date;
        @Schema(description = "수행률(%)")
        private Integer performance;
        @Schema(description = "할일/습관 리스트")
        private List<ScheduleListResponse> schedules;
    }
    @Schema(description = "성적표 통계 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatisticsResponse {

        @Schema(description = "스케줄 전체 수행 일수")
        private Long fullDoneCount;
        @Schema(description = "스케줄 부분 수행 일수")
        private Long partialDoneCount;
        @Schema(description = "전체 평균 수행률(%)")
        private Integer performanceAvg;
        @Schema(description = "할일 수행 일수")
        private Long todoDoneCount;
        @Schema(description = "습관 수행 일수")
        private Long routineDoneCount;
        @Schema(description = "일기장 작성 횟수")
        private Long diaryCount;
        @Schema(description = "유저 서비스 이용 일수")
        private Integer togetherCount;
    }
    @Schema(description = "월간 주간 평가 리스트 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GradesOfMonthResponse {

        @Schema(description = "월의 주차 번호\n\n월요일 ~ 일요일을 한 주차로 계산합니다.")
        private Integer weekOfMonth;
        @Schema(description = "주간 평가 등급\n\n1:수, 2:우, 3:미, 4:양, 5:가")
        private Optional<Integer> gradeOfWeek;
    }

    @Schema(description = "수행률 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Performance {
        @Schema(description = "수행률(%)")
        private Integer avg;
        @Schema(description = "날짜")
        private LocalDate date;
    }

    @Schema(description = "상장 달성도 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AwardResponse {
        @Schema(description = "상장 달성도(오름차순)", defaultValue = "3", allowableValues = {"1", "2", "3",
            "4"})
        private Integer level;
    }
}
