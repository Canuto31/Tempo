package com.ashvyn.tempo.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCategoryRequest(UUID ownerId, @NotBlank @Size(max = 100) String name) {
}
