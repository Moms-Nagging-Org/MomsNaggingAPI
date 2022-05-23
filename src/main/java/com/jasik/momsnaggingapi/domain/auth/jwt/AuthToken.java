package com.jasik.momsnaggingapi.domain.auth.jwt;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {
    @Getter
    private final String token;
    private final Key key;

    public AuthToken(Key key, Long id, String provider, String email, String personalId) {
        this.key = key;
        this.token = createToken(key, id, provider, email, personalId);
    }

    // 토큰 생성
    public String createToken(Key key, Long id, String provider, String email, String personalId) {
        // header
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");

        // subject
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", personalId);
        claims.put("provider", provider);
        claims.put("email", email);

        Date now = new Date();

        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setSubject(String.valueOf(id))
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(now)
                .compact();
    }

}
