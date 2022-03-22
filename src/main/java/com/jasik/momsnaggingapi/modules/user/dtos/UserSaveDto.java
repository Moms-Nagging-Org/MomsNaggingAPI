package com.jasik.momsnaggingapi.modules.user.dtos;

import com.jasik.momsnaggingapi.modules.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
public class UserSaveDto {

    @Schema(description = "유저 이름", example = "gildong")
    String userName;
    @Schema(description = "유저 이메일", example = "gildong@gmail.com", required = true)
    String userEmail;

    @Builder
    public UserSaveDto(String userName, String userEmail){
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public User toEntity(){
        return User.builder()
                .email(userEmail)
                .username(userName)
                .build();
    }
}