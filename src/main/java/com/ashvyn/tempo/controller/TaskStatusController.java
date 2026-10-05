package com.ashvyn.tempo.controller;

import com.ashvyn.tempo.dto.status.CreateTaskStatusRequest;
import com.ashvyn.tempo.dto.status.TaskStatusResponse;
import com.ashvyn.tempo.dto.status.UpdateTaskStatusRequest;
import com.ashvyn.tempo.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task-statuses")
@Tag(name = "Task Statuses", description = "Global and user task status endpoints")
/** Exposes configurable global and user-owned task statuses. */
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    /** Creates a task status and assigns its display position. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create task status")
    public TaskStatusResponse create(@Valid @RequestBody CreateTaskStatusRequest request) {
        return taskStatusService.create(request);
    }

    /** Returns a task status by identifier. */
    @GetMapping("/{id}")
    @Operation(summary = "Get task status")
    public TaskStatusResponse get(@PathVariable UUID id) {
        return taskStatusService.get(id);
    }

    /** Lists statuses ordered by position and optionally filtered by scope. */
    @GetMapping
    @Operation(summary = "List task statuses")
    public List<TaskStatusResponse> list(@RequestParam(required = false) UUID ownerId,
                                         @RequestParam(defaultValue = "false") boolean global) {
        return taskStatusService.list(ownerId, global);
    }

    /** Updates the editable status properties. */
    @PutMapping("/{id}")
    @Operation(summary = "Update task status")
    public TaskStatusResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTaskStatusRequest request) {
        return taskStatusService.update(id, request);
    }

    /** Deletes a status; referenced statuses produce a conflict response. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete task status")
    public void delete(@PathVariable UUID id) {
        taskStatusService.delete(id);
    }
}
