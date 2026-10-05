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
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final UserService userService;

    public TaskStatusService(TaskStatusRepository taskStatusRepository, UserService userService) {
        this.taskStatusRepository = taskStatusRepository;
        this.userService = userService;
    }

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

    @Transactional(readOnly = true)
    public TaskStatusResponse get(UUID id) {
        return toResponse(requireStatus(id));
    }

    @Transactional(readOnly = true)
    public List<TaskStatusResponse> list(UUID ownerId, boolean global) {
        List<TaskStatus> statuses = global ? taskStatusRepository.findByOwnerIsNullOrderByPosition()
                : ownerId == null ? taskStatusRepository.findAllByOrderByPosition()
                : taskStatusRepository.findByOwnerIdOrderByPosition(ownerId);
        return statuses.stream().map(this::toResponse).toList();
    }

    @Transactional
    public TaskStatusResponse update(UUID id, UpdateTaskStatusRequest request) {
        TaskStatus status = requireStatus(id);
        status.setName(request.name());
        status.setPosition(request.position());
        status.setDefaultStatus(request.defaultStatus());
        status.setUpdatedAt(Instant.now());
        return toResponse(taskStatusRepository.save(status));
    }

    @Transactional
    public void delete(UUID id) {
        taskStatusRepository.delete(requireStatus(id));
    }

    public TaskStatus requireStatus(UUID id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found: " + id));
    }

    private TaskStatusResponse toResponse(TaskStatus status) {
        return new TaskStatusResponse(status.getId(), status.getOwner() == null ? null : status.getOwner().getId(),
                status.getName(), status.getPosition(), status.isDefaultStatus(), status.getCreatedAt(),
                status.getUpdatedAt());
    }
}
