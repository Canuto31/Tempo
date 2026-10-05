package com.ashvyn.tempo.dto.user;

import com.ashvyn.tempo.enums.AuthProvider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 255) String password,
        @NotNull AuthProvider authProvider,
        @Size(max = 500) String profileImage
) {
}
