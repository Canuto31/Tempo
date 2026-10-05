package com.ashvyn.tempo.dto.user;

import com.ashvyn.tempo.enums.AuthProvider;

import java.util.Date;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String username,
        String email,
        AuthProvider authProvider,
        String profileImage,
        Date createdAt,
        Date updatedAt
) {
}
