package com.ashvyn.tempo.dto.status;

import java.time.Instant;
import java.util.UUID;

public record TaskStatusResponse(
        UUID id,
        UUID ownerId,
        String name,
        Integer position,
        boolean defaultStatus,
        Instant createdAt,
        Instant updatedAt
) {
}
