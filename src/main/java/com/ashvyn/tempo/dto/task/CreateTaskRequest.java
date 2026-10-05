package com.ashvyn.tempo.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 5000) String description,
        @Size(max = 10000) String notes,
        UUID personalOwnerId,
        UUID projectId,
        @NotNull UUID responsibleUserId,
        UUID parentTaskId,
        UUID categoryId,
        @NotNull UUID statusId,
        LocalDate deadline,
        @NotNull @PositiveOrZero Long estimatedTimeSeconds,
        @NotNull @PositiveOrZero Integer pokerPoints
) {
}
