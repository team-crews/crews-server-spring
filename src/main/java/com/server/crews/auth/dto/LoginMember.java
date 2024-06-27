package com.server.crews.auth.dto;

import com.server.crews.auth.domain.Role;

public record LoginMember(String email, Role role, Long recruitmentId) {
}
