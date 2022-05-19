package com.jasik.momsnaggingapi.domain.user;

import com.jasik.momsnaggingapi.domain.common.BaseTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int naggingLevel;
    private String nickName;
    private String email;
    private String provider;
    private String providerCode;
    private String personalId;
    private String device;
    private String profileImage;

    @Column(columnDefinition = "varchar(30) default 'MEMBER'")
    private String role;
    @Column(columnDefinition = "TEXT")
    private String statusMsg;

    @Column(columnDefinition = "boolean default true")
    private boolean allowGeneralNotice;
    @Column(columnDefinition = "boolean default true")
    private boolean allowTodoNotice;
    @Column(columnDefinition = "boolean default true")
    private boolean allowRoutineNotice;
    @Column(columnDefinition = "boolean default true")
    private boolean allowWeeklyNotice;
    @Column(columnDefinition = "boolean default true")
    private boolean allowOtherNotice;

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

    public User(String subject, String s, Collection<? extends GrantedAuthority> authorities) {
        super();
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
        private boolean allowGeneralNotice;
        private boolean allowTodoNotice;
        private boolean allowRoutineNotice;
        private boolean allowWeeklyNotice;
        private boolean allowOtherNotice;
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
        @Schema(description = "소셜로그인 플랫폼")
        private String provider;
        @Schema(description = "사용자 이메일")
        private String email;
        @Schema(description = "소셜 코드")
        private String code;
        @Schema(description = "디바이스")
        private String device;
        @Schema(description = "아이디")
        private String personalId;
        @Schema(description = "호칭")
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {
        private String nickName;
        private int naggingLevel;
        private boolean allowGeneralNotice;
        private boolean allowTodoNotice;
        private boolean allowRoutineNotice;
        private boolean allowWeeklyNotice;
        private boolean allowOtherNotice;
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
