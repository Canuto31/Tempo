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
public class LabelService {

    private final LabelRepository labelRepository;
    private final UserService userService;

    public LabelService(LabelRepository labelRepository, UserService userService) {
        this.labelRepository = labelRepository;
        this.userService = userService;
    }

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

    @Transactional(readOnly = true)
    public LabelResponse get(UUID id) {
        return toResponse(requireActive(id));
    }

    @Transactional(readOnly = true)
    public List<LabelResponse> list(UUID ownerId, boolean global) {
        List<Label> labels = global ? labelRepository.findByOwnerIsNullAndDeletedAtIsNull()
                : ownerId == null ? labelRepository.findByDeletedAtIsNull()
                : labelRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
        return labels.stream().map(this::toResponse).toList();
    }

    @Transactional
    public LabelResponse update(UUID id, UpdateLabelRequest request) {
        Label label = requireActive(id);
        label.setName(request.name());
        label.setUpdatedAt(Instant.now());
        return toResponse(labelRepository.save(label));
    }

    @Transactional
    public void delete(UUID id) {
        Label label = requireActive(id);
        label.setDeletedAt(Instant.now());
        label.setUpdatedAt(Instant.now());
        labelRepository.save(label);
    }

    public Label requireActive(UUID id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
        if (label.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Label not found: " + id);
        }
        return label;
    }

    private LabelResponse toResponse(Label label) {
        return new LabelResponse(label.getId(), label.getOwner() == null ? null : label.getOwner().getId(),
                label.getName(), label.getCreatedAt(), label.getUpdatedAt());
    }
}
