package com.ashvyn.tempo.dto.project;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        UUID ownerId,
        UUID parentProjectId,
        String name,
        String description,
        LocalDate deadline,
        Instant createdAt,
        Instant updatedAt
) {
}
