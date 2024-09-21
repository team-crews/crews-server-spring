package com.server.crews.auth.dto;

import com.server.crews.auth.domain.Role;

public record LoginUser(Long userId, Role role) {
}
