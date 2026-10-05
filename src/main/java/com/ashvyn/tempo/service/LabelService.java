package com.ashvyn.tempo.service;

import com.ashvyn.tempo.dto.label.CreateLabelRequest;
import com.ashvyn.tempo.dto.label.LabelResponse;
import com.ashvyn.tempo.dto.label.UpdateLabelRequest;
import com.ashvyn.tempo.entity.Label;
import com.ashvyn.tempo.exception.ResourceNotFoundException;
import com.ashvyn.tempo.repository.LabelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
/** Manages global and user-owned labels with soft-delete semantics. */
public class LabelService {

    private final LabelRepository labelRepository;
    private final UserService userService;

    public LabelService(LabelRepository labelRepository, UserService userService) {
        this.labelRepository = labelRepository;
        this.userService = userService;
    }

    /** Creates a global label or resolves its user owner when provided. */
    @Transactional
    public LabelResponse create(CreateLabelRequest request) {
        Instant now = Instant.now();
        Label label = new Label();
        label.setOwner(request.ownerId() == null ? null : userService.requireUser(request.ownerId()));
        label.setName(request.name());
        label.setCreatedAt(now);
        label.setUpdatedAt(now);
        return toResponse(labelRepository.save(label));
    }

    /** Returns an active label by identifier. */
    @Transactional(readOnly = true)
    public LabelResponse get(UUID id) {
        return toResponse(requireActive(id));
    }

    /** Lists active labels for all scopes, one owner, or the global scope. */
    @Transactional(readOnly = true)
    public List<LabelResponse> list(UUID ownerId, boolean global) {
        List<Label> labels = global ? labelRepository.findByOwnerIsNullAndDeletedAtIsNull()
                : ownerId == null ? labelRepository.findByDeletedAtIsNull()
                : labelRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
        return labels.stream().map(this::toResponse).toList();
    }

    /** Changes the label name and refreshes its update timestamp. */
    @Transactional
    public LabelResponse update(UUID id, UpdateLabelRequest request) {
        Label label = requireActive(id);
        label.setName(request.name());
        label.setUpdatedAt(Instant.now());
        return toResponse(labelRepository.save(label));
    }

    /** Soft-deletes the label while preserving its database row. */
    @Transactional
    public void delete(UUID id) {
        Label label = requireActive(id);
        label.setDeletedAt(Instant.now());
        label.setUpdatedAt(Instant.now());
        labelRepository.save(label);
    }

    /** Resolves an active label for other domain operations. */
    public Label requireActive(UUID id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
        if (label.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Label not found: " + id);
        }
        return label;
    }

    /** Converts owner relationships into a nullable owner identifier. */
    private LabelResponse toResponse(Label label) {
        return new LabelResponse(label.getId(), label.getOwner() == null ? null : label.getOwner().getId(),
                label.getName(), label.getCreatedAt(), label.getUpdatedAt());
    }
}
