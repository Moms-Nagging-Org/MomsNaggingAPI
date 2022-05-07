package com.jasik.momsnaggingapi.domain.schedule;

import com.jasik.momsnaggingapi.domain.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
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
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        @Schema(description = "추천 종류 id", defaultValue = "1")
        private Long id;
        @Schema(description = "추천 종류 이름", defaultValue = "운동하기")
        private String categoryName;
    }

}
