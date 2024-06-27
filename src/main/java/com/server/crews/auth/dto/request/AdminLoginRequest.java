package com.server.crews.auth.dto.request;

public record AdminLoginRequest(
        String email,
        String password
) {

}
