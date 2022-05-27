package com.jasik.momsnaggingapi.domain.user;

import com.jasik.momsnaggingapi.infra.common.BaseTime;
import com.jasik.momsnaggingapi.infra.common.StringListConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
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
@Getter @Setter
@NoArgsConstructor
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int naggingLevel = 1;
    private String nickName;
    private String email;
    private String provider;
    private String providerCode;
    private String personalId;
    private String device;
    private String profileImage;

    @Column(columnDefinition = "varchar(30) default 'MEMBER'")
    private String role = "MEMBER";
    @Column(columnDefinition = "TEXT")
    private String statusMsg = "";

    @Column(columnDefinition = "boolean default true")
    private Boolean allowGeneralNotice = true;
    @Column(columnDefinition = "boolean default true")
    private Boolean allowTodoNotice = true;
    @Column(columnDefinition = "boolean default true")
    private Boolean allowRoutineNotice = true;
    @Column(columnDefinition = "boolean default true")
    private Boolean allowWeeklyNotice = true;
    @Column(columnDefinition = "boolean default true")
    private Boolean allowOtherNotice = true;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "json")
    private List<String> routineOrder;

    public void updateRoutineOrder(List<String> newOrder) {
        this.routineOrder = newOrder;
    }

    @Builder
    public User(int naggingLevel, String nickName, String email,
        String provider, String providerCode, String personalId, String device) {
        this.naggingLevel = naggingLevel;
        this.nickName = nickName;
        this.email = email;
        this.provider = provider;
        this.providerCode = providerCode;
        this.personalId = personalId;
        this.device = device;
    }

    @Schema(description = "사용자 관련 기본 응답 클래스")
    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
    }

    @Schema(description = "사용자 조회 시 응답 클래스")
    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponse {
        private Long id;
        private String email;
        private String provider;
        private String nickName;
        private String personalId;
        private int naggingLevel;
        private String device;
        private Boolean allowGeneralNotice;
        private Boolean allowTodoNotice;
        private Boolean allowRoutineNotice;
        private Boolean allowWeeklyNotice;
        private Boolean allowOtherNotice;
    }

    @Schema(description = "로그인 요청 클래스")
    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthRequest {
        @Schema(description = "소셜로그인 플랫폼")
        private String provider;
        @Schema(description = "플랫폼 인증 코드")
        private String code;
    }

    @Schema(description = "로그인/회원가입 응답 클래스")
    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthResponse {
        @Schema(description = "JWT 토큰")
        private String token;
    }

    @Schema(description = "회원가입 요청 클래스")
    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRequest {
        @Schema(description = "소셜로그인 플랫폼(Kakao | Google)")
        private String provider;
        @Schema(description = "사용자 이메일")
        private String email;
        @Schema(description = "소셜 코드")
        private String code;
        @Schema(description = "디바이스(IOS | AOS)")
        private String device;
        @Schema(description = "아이디")
        private String personalId;
        @Schema(description = "호칭")
        private String nickname;
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {
        private String nickName;
        private int naggingLevel;
        private Boolean allowGeneralNotice;
        private Boolean allowTodoNotice;
        private Boolean allowRoutineNotice;
        private Boolean allowWeeklyNotice;
        private Boolean allowOtherNotice;
    }

    @Schema(description = "아이디 중복확인 응답 클래스")
    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidateResponse {
        @Schema(description = "존재 유무")
        private Boolean isExist;
    }
}
