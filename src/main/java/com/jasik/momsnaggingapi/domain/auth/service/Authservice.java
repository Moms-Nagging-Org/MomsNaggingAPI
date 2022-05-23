package com.jasik.momsnaggingapi.domain.auth.service;

import com.jasik.momsnaggingapi.domain.auth.jwt.AuthTokenProvider;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class Authservice {
//    private final UserService userService;
    private final AuthTokenProvider authTokenProvider;

    public String getPersonalId(String token) {
        Claims claims = authTokenProvider.getTokenClaim(token);
        if (claims == null) {
            return null;
        }

        return claims.getSubject();
    }

//    public Long getUserId(String token) {
//        Claims claims = authTokenProvider.getTokenClaim(token);
//        if (claims == null) {
//            return null;
//        }
//
//        try {
//            return userService.findUserIdByPersonalId(claims.getSubject());
//        } catch (NullPointerException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");
//        }
//    }
//
//    public User getUser(String token) {
//        Claims claims = authTokenProvider.getTokenClaim(token);
//        if (claims == null) {
//            return null;
//        }
//
//        return userService.findUserByPersonalId(claims.getSubject()).orElseThrow(null);
//    }
}
