package com.jasik.momsnaggingapi.domain.schedule;

import com.jasik.momsnaggingapi.domain.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import lombok.Setter;

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

        private Long id;
        private String categoryName;
    }

}
