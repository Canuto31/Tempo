package com.ashvyn.tempo.dto.label;

import java.time.Instant;
import java.util.UUID;

public record LabelResponse(UUID id, UUID ownerId, String name, Instant createdAt, Instant updatedAt) {
}
