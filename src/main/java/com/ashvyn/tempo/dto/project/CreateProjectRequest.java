package com.ashvyn.tempo.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectRequest(
        @NotNull UUID ownerId,
        UUID parentProjectId,
        @NotBlank @Size(max = 150) String name,
        @Size(max = 2000) String description,
        LocalDate deadline
) {
}
