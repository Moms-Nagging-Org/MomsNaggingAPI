package com.jasik.momsnaggingapi.domain.schedule;

import com.jasik.momsnaggingapi.domain.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Category extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;

    @Column(columnDefinition = "boolean default true", name = "is_used")
    private boolean used;

    @Builder
    public Category(String categoryName, boolean used) {

        this.categoryName = categoryName;
        this.used = used;
    }

    @Schema(description = "스케줄 추천 종류 조회 시 응답 클래스")
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryResponse {

        @Schema(description = "스케줄 추천 종류 ID", defaultValue = "2")
        private Long id;

        @Schema(description = "스케줄 추천 종류 이름", defaultValue = "운동")
        private String categoryName;
    }
}
