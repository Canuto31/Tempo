package com.ashvyn.tempo.service;

import com.ashvyn.tempo.dto.status.CreateTaskStatusRequest;
import com.ashvyn.tempo.dto.status.TaskStatusResponse;
import com.ashvyn.tempo.dto.status.UpdateTaskStatusRequest;
import com.ashvyn.tempo.entity.TaskStatus;
import com.ashvyn.tempo.exception.ResourceNotFoundException;
import com.ashvyn.tempo.repository.TaskStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
/** Manages configurable task statuses and their display order. */
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final UserService userService;

    public TaskStatusService(TaskStatusRepository taskStatusRepository, UserService userService) {
        this.taskStatusRepository = taskStatusRepository;
        this.userService = userService;
    }

    /** Creates a global or user-owned status with its initial position. */
    @Transactional
    public TaskStatusResponse create(CreateTaskStatusRequest request) {
        Instant now = Instant.now();
        TaskStatus status = new TaskStatus();
        status.setOwner(request.ownerId() == null ? null : userService.requireUser(request.ownerId()));
        status.setName(request.name());
        status.setPosition(request.position());
        status.setDefaultStatus(request.defaultStatus());
        status.setCreatedAt(now);
        status.setUpdatedAt(now);
        return toResponse(taskStatusRepository.save(status));
    }

    /** Retrieves a task status by identifier. */
    @Transactional(readOnly = true)
    public TaskStatusResponse get(UUID id) {
        return toResponse(requireStatus(id));
    }

    /** Lists statuses ordered by position and optionally filtered by scope. */
    @Transactional(readOnly = true)
    public List<TaskStatusResponse> list(UUID ownerId, boolean global) {
        List<TaskStatus> statuses = global ? taskStatusRepository.findByOwnerIsNullOrderByPosition()
                : ownerId == null ? taskStatusRepository.findAllByOrderByPosition()
                : taskStatusRepository.findByOwnerIdOrderByPosition(ownerId);
        return statuses.stream().map(this::toResponse).toList();
    }

    /** Updates status metadata and its modification timestamp. */
    @Transactional
    public TaskStatusResponse update(UUID id, UpdateTaskStatusRequest request) {
        TaskStatus status = requireStatus(id);
        status.setName(request.name());
        status.setPosition(request.position());
        status.setDefaultStatus(request.defaultStatus());
        status.setUpdatedAt(Instant.now());
        return toResponse(taskStatusRepository.save(status));
    }

    /** Deletes an unreferenced status from persistence. */
    @Transactional
    public void delete(UUID id) {
        taskStatusRepository.delete(requireStatus(id));
    }

    /** Resolves a status entity for assignment to a task. */
    public TaskStatus requireStatus(UUID id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found: " + id));
    }

    /** Maps a status and its optional owner to the API response. */
    private TaskStatusResponse toResponse(TaskStatus status) {
        return new TaskStatusResponse(status.getId(), status.getOwner() == null ? null : status.getOwner().getId(),
                status.getName(), status.getPosition(), status.isDefaultStatus(), status.getCreatedAt(),
                status.getUpdatedAt());
    }
}
