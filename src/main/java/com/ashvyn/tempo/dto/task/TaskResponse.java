package com.ashvyn.tempo.dto.task;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        String notes,
        UUID personalOwnerId,
        UUID projectId,
        UUID responsibleUserId,
        UUID parentTaskId,
        UUID categoryId,
        UUID statusId,
        LocalDate deadline,
        Long estimatedTimeSeconds,
        Integer pokerPoints,
        Instant createdAt,
        Instant completedAt,
        Instant updatedAt
) {
}
