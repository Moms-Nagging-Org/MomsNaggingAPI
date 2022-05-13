package com.jasik.momsnaggingapi.domain.grade;

import com.jasik.momsnaggingapi.domain.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
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

        @Schema(description = "평가 단계(오름차순)", defaultValue = "3", allowableValues = {"1", "2", "3",
            "4", "5"})
        private int gradeLevel;
        @Schema(description = "년도", defaultValue = "2022")
        private int createdYear;
        @Schema(description = "주차", defaultValue = "15")
        private int createdWeek;
        @Schema(description = "새롭게 달성한 상장의 단계, 0인 경우 새로 달성한 상장 없음.", defaultValue = "0")
        private int awardLevel;
    }

}
