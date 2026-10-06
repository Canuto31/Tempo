package com.ashvyn.tempo.dto.task;

import jakarta.validation.constraints.NotNull;

/** Request used by a checkbox to complete or reopen a task. */
public record UpdateTaskCompletionRequest(@NotNull Boolean completed) {
}
