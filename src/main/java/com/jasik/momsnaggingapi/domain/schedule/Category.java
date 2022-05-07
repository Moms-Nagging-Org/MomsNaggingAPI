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

    private Long userId;

    @Column(columnDefinition = "boolean default true", name = "is_used")
    private boolean used;

    @Builder
    public Category(String categoryName, Long userId, boolean used) {

        this.categoryName = categoryName;
        this.userId = userId;
        this.used = used;
    }

    public void initUserId(Long userId) {
        this.userId = userId;
    }

    @Schema(description = "습관 추천 종류 생성 시 요청 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryRequest {

        @Schema(description = "추천 종류 이름", defaultValue = "운동하기")
        private String categoryName;
        @Schema(description = "추천 종류 활성화 여부", defaultValue = "true", allowableValues = {"true",
                "false"})
        private boolean used;

    }

    @Schema(description = "습관 추천 종류 조회 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryResponse {

        @Schema(description = "추천 종류 id", defaultValue = "1")
        private Long id;
        @Schema(description = "추천 종류 이름", defaultValue = "운동하기")
        private String categoryName;
    }

}
