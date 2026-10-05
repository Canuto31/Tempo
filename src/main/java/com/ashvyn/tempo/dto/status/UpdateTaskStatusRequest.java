package com.ashvyn.tempo.dto.status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateTaskStatusRequest(
        @NotBlank @Size(max = 50) String name,
        @NotNull @PositiveOrZero Integer position,
        boolean defaultStatus
) {
}
