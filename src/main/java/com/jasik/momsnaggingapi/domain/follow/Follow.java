package com.jasik.momsnaggingapi.domain.follow;

import com.jasik.momsnaggingapi.domain.follow.Follow.FollowResponse;
import com.jasik.momsnaggingapi.infra.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
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
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NamedNativeQuery;

@Entity
@NamedNativeQuery(
    name = "findFollowers",
    query =
    "select b.id as userId, b.nick_name as nickName, b.personal_id as personalId, b.status_msg as statusMsg\n"
    + "from follow a inner join user b on a.from_user = b.id\n"
    + "where a.to_user = :userId\n"
    + "and a.is_blocked = False\n"
    + "order by a.created_at desc;",
    resultSetMapping = "FollowResponse")
@SqlResultSetMapping(name = "FollowResponse", classes = @ConstructorResult(targetClass = FollowResponse.class, columns = {
    @ColumnResult(name = "userId", type = Long.class),
    @ColumnResult(name = "nickName", type = String.class),
    @ColumnResult(name = "personalId", type = String.class),
    @ColumnResult(name = "statusMsg", type = String.class)
}))
@NamedNativeQuery(
    name = "findFollowings",
    query =
        "select b.id as userId, b.nick_name as nickName, b.personal_id as personalId, b.status_msg as statusMsg\n"
            + "from follow a inner join user b on a.to_user = b.id\n"
            + "where a.from_user = :userId\n"
            + "and a.is_blocked = False\n"
            + "order by a.created_at desc;",
    resultSetMapping = "FollowResponse")
@Getter
@Setter
@NoArgsConstructor
public class Follow extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromUser;
    private Long toUser;
    @Column(columnDefinition = "boolean default false")
    private boolean isBlocked;

    public boolean checkBlocked() {
        return isBlocked;
    }

    @Builder
    public Follow(Long fromUser, Long toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    @Schema(description = "팔로워 조회 시 응답 클래스")
    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FollowResponse {

        @Schema(description = "유저 번호", defaultValue = "2")
        private Long userId;

        @Schema(description = "유저 아이디", defaultValue = "elonmusk")
        private String personalId;

        @Schema(description = "유저 닉네임", defaultValue = "Elon Musk")
        private String nickName;

        @Schema(description = "유저 메시지", defaultValue = "Go to mars")
        private String statusMsg;

    }
}
