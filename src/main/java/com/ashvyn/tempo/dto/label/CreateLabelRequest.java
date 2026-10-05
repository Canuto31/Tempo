package com.ashvyn.tempo.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateLabelRequest(UUID ownerId, @NotBlank @Size(max = 100) String name) {
}
