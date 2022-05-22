package com.jasik.momsnaggingapi.domain.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {
    @Getter
    private final String token;
//    @Value("${jwt.secret}")
    private final Key key;
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
    public AuthToken(Key key, String provider, String email, String personalId) {
        this.key = key;
        this.token = createToken(provider, email, personalId);
    }

//    public void setKey(
//            @Value("${jwt.secret}") String secretKey) {
//        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
//    }

    // 토큰 생성
    public String createToken(String provider, String email, String personalId) {
//        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        // header
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");

        // claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("provider", provider);
        claims.put("id", personalId);

        Date now = new Date();

        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(now)
                .compact();
    }

}
