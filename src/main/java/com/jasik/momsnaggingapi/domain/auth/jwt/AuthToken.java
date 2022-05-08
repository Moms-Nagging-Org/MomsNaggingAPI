package com.jasik.momsnaggingapi.domain.auth.jwt;

import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthToken {
    public String createToken(String provider, String email, String id) {
        // header
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        // claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("id", id);
        claims.put("provider", provider);

//        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();

        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setIssuedAt(now)
                .compact();
    }
}
