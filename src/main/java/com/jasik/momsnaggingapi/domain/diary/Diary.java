package com.jasik.momsnaggingapi.domain.diary;

import com.jasik.momsnaggingapi.infra.common.BaseTime;
import com.jasik.momsnaggingapi.domain.diary.Diary.DailyDiary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NamedNativeQuery;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@NamedNativeQuery(name = "findDailyDiary", query =
    "select IF(a.title is null and a.context is null, false, true) as diaryExists, b.diaryDate\n"
        + "from\n" + "    (select *\n" + "     from diary\n" + "     where user_id = :userId\n"
        + "       and diary_date >= :startDate\n" + "       and diary_date <= :endDate\n"
        + "     ) a\n" + "        right outer join (\n" + "        SELECT\n"
        + "            DATE_FORMAT(DATE_ADD(:startDate, INTERVAL seq - 1 DAY), '%Y-%m-%d') AS diaryDate\n"
        + "        FROM (SELECT @num \\:= @num + 1 AS seq\n"
        + "              FROM information_schema.tables a\n"
        + "                 , information_schema.tables b\n"
        + "                 , (SELECT @num \\:= 0) c\n" + "             ) T\n"
        + "        WHERE seq <=  DATEDIFF(:endDate, :startDate) + 1\n"
        + "    ) b on a.diary_date = b.diaryDate;", resultSetMapping = "DailyDiary")
@SqlResultSetMapping(name = "DailyDiary", classes = @ConstructorResult(targetClass = DailyDiary.class, columns = {
    @ColumnResult(name = "diaryExists", type = Boolean.class),
    @ColumnResult(name = "diaryDate", type = LocalDate.class)}))
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
    @Column(columnDefinition = "TEXT")
    private String context;
    private LocalDate diaryDate;

    @Builder
    public Diary(Long userId, String title, String context, LocalDate diaryDate) {
        this.userId = userId;
        this.title = title;
        this.context = context;
        this.diaryDate = diaryDate;
    }

    public void updateDiary(String title, String context) {
        this.title = title;
        this.context = context;
    }

    public void initUser(Long userId) {
        this.userId = userId;
    }

    @Schema(description = "????????? ?????? ??? ?????? ?????????")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiaryRequest {
        @Schema(description = "????????? ??????", defaultValue = "???????????? ??????")
        @NotNull
        private String title;
        @Schema(description = "????????? ??????", defaultValue = "???????????? ????????? ?????????. ????????????.")
        @NotNull
        private String context;
        @Schema(description = "????????? ??????")
        @NotBlank
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate diaryDate;
    }

    @Schema(description = "????????? ?????? ??? ?????? ?????????")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiaryResponse {

        @Schema(description = "????????? ??????", defaultValue = "???????????? ??????")
        private String title;

        @Schema(description = "????????? ??????", defaultValue = "???????????? ????????? ?????????. ????????????.")
        private String context;

        @Schema(description = "????????? ??????")
        @NotBlank
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate diaryDate;

        @Schema(description = "?????? ?????? ??????", defaultValue = "true", allowableValues = {"true",
            "false"})
        private boolean today;
    }
    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyDiary {
        @Schema(description = "????????? ?????? ??????")
        private boolean diaryExists;
        @Schema(description = "????????? ??????")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate diaryDate;
    }

    @Schema(description = "?????? ?????? ????????? ?????? ?????? ?????? ??? ?????? ?????????")
    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyResponse extends DailyDiary {
        @Schema(description = "????????? ??????")
        private boolean isHoliday;

//        @SuperBuilder
//        public DailyResponse(boolean diaryExists, LocalDate diaryDate, boolean isHoliday) {
//            this.diaryExists = diaryExists;
//            this.diaryDate = diaryDate;
//            this.isHoliday = isHoliday;
//        }
    }
}
