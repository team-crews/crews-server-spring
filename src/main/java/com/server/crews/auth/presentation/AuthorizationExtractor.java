package com.server.crews.auth.presentation;

import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.GeneralErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.springframework.http.HttpHeaders;

public class AuthorizationExtractor {
    public static final String BEARER_TYPE = "Bearer ";

    public static String extract(final HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(authorizationHeader)) {
            throw new CrewsException(GeneralErrorCode.NO_TOKEN);
        }

        validateAuthorizationFormat(authorizationHeader);
        return authorizationHeader.substring(BEARER_TYPE.length()).trim();
    }

    private static void validateAuthorizationFormat(final String authorizationHeader) {
        if (!authorizationHeader.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            throw new CrewsException(GeneralErrorCode.INVALID_TOKEN);
        }
    }
}
