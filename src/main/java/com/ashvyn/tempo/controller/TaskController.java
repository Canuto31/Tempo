package com.ashvyn.tempo.controller;

import com.ashvyn.tempo.dto.task.CreateTaskRequest;
import com.ashvyn.tempo.dto.task.TaskResponse;
import com.ashvyn.tempo.dto.task.UpdateTaskRequest;
import com.ashvyn.tempo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Task management endpoints")
/** Exposes task management while delegating all domain rules to {@link TaskService}. */
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /** Creates a personal or project task from a validated request. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create task")
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.create(request);
    }

    /** Returns an active task by identifier. */
    @GetMapping("/{id}")
    @Operation(summary = "Get active task")
    public TaskResponse get(@PathVariable UUID id) {
        return taskService.get(id);
    }

    /** Lists active tasks and accepts at most one relationship filter. */
    @GetMapping
    @Operation(summary = "List active tasks", description = "Accepts at most one simple relationship filter")
    public List<TaskResponse> list(@RequestParam(required = false) UUID personalOwnerId,
                                   @RequestParam(required = false) UUID projectId,
                                   @RequestParam(required = false) UUID responsibleUserId,
                                   @RequestParam(required = false) UUID parentTaskId) {
        return taskService.list(personalOwnerId, projectId, responsibleUserId, parentTaskId);
    }

    /** Replaces the editable fields and relationships of an active task. */
    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.update(id, request);
    }

    /** Marks a task as deleted through its {@code deletedAt} field. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete task")
    public void delete(@PathVariable UUID id) {
        taskService.delete(id);
    }
}
