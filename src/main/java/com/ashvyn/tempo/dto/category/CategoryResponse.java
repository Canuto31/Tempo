package com.ashvyn.tempo.dto.category;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(UUID id, UUID ownerId, String name, Instant createdAt, Instant updatedAt) {
}
