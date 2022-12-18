package com.jasik.momsnaggingapi.domain.schedule;

import com.jasik.momsnaggingapi.infra.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reaction extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int typeId;

    private Long fromUser;

    private Long toUser;

    private LocalDate scheduleDate;

    @Builder
    public Reaction(int typeId, Long fromUser, Long toUser, LocalDate scheduleDate) {
        this.typeId = typeId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.scheduleDate = scheduleDate;
    }

    @Schema(description = "반응 추가 시 응답 클래스")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReactionResponse {

        @Schema(description = "반응 종류 ID, 0부터 시작", defaultValue = "1")
        private int typeId;
        @Schema(description = "대상 유저 번호", defaultValue = "9")
        private Long toUser;
        @Schema(description = "대상 일자", defaultValue = "2022-12-25")
        private LocalDate scheduleDate;
    }

    @Schema(description = "스케줄 일자의 반응 조회 시 응답 클래스")
    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReactionInfoResponse {

        @Schema(description = "전체 반응 수", defaultValue = "24")
        int reactionTotalCnt;
        @Schema(description = "전체 반응 목록")
        List<ReactionInfo> reactionTotalInfos;
        @Schema(description = "유형별 반응 목록")
        List<ReactionInfoByType> reactionInfos;
        @Schema(description = "유저가 반응한 유형의 ID 목록")
        List<Integer> reactedTypeIds;

    }

    @Schema(description = "스케줄 일자의 반응한 유저 정보")
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ReactionInfo {
        @Schema(description = "반응한 종류 번호")
        private int typeId;
        @Schema(description = "반응한 유저 번호")
        private Long userId;
        @Schema(description = "반응 유저 ID")
        private String personalId;
    }

    @Schema(description = "스케줄 일자의 반응 유형별 정보")
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ReactionInfoByType {
        @Schema(description = "반응 종류 번호")
        private int typeId;
        @Schema(description = "반응 수")
        private int cnt;
        @Schema(description = "스케줄 일자의 반응한 유저 정보")
        private List<ReactionInfo> reactionInfos;
    }

}
