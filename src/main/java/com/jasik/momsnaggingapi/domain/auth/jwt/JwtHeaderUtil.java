package com.jasik.momsnaggingapi.domain.auth.jwt;

import javax.servlet.http.HttpServletRequest;

public class JwtHeaderUtil {
    private final static String AUTHORIZATION_HEADER = "Authorization";

    public static String getAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(AUTHORIZATION_HEADER);

        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith("Bearer ")) {
            return headerValue.substring(7);
        }

        return null;
    }
}
