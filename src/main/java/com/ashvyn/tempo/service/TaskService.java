package com.ashvyn.tempo.service;

import com.ashvyn.tempo.dto.task.CreateTaskRequest;
import com.ashvyn.tempo.dto.task.TaskResponse;
import com.ashvyn.tempo.dto.task.UpdateTaskRequest;
import com.ashvyn.tempo.entity.Task;
import com.ashvyn.tempo.exception.BusinessRuleException;
import com.ashvyn.tempo.exception.ResourceNotFoundException;
import com.ashvyn.tempo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
/** Implements task lifecycle operations and core Sprint 1 task rules. */
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final CategoryService categoryService;
    private final TaskStatusService taskStatusService;

    public TaskService(TaskRepository taskRepository, UserService userService, ProjectService projectService,
                       CategoryService categoryService, TaskStatusService taskStatusService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.projectService = projectService;
        this.categoryService = categoryService;
        this.taskStatusService = taskStatusService;
    }

    /** Creates a task after validating its exclusive personal/project context. */
    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        validateContext(request.personalOwnerId(), request.projectId());
        Instant now = Instant.now();
        Task task = new Task();
        apply(task, request.title(), request.description(), request.notes(), request.personalOwnerId(),
                request.projectId(), request.responsibleUserId(), request.parentTaskId(), request.categoryId(),
                request.statusId(), request.deadline(), request.estimatedTimeSeconds(), request.pokerPoints());
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        Task savedTask = taskRepository.save(task);
        synchronizeAncestors(savedTask.getParentTask(), now);
        return toResponse(savedTask);
    }

    /** Retrieves an active task and maps its relationships to identifiers. */
    @Transactional(readOnly = true)
    public TaskResponse get(UUID id) {
        return toResponse(requireActive(id));
    }

    /** Lists active tasks using no filter or one simple relationship filter. */
    @Transactional(readOnly = true)
    public List<TaskResponse> list(UUID personalOwnerId, UUID projectId, UUID responsibleUserId, UUID parentTaskId) {
        long filterCount = Stream.of(personalOwnerId, projectId, responsibleUserId, parentTaskId)
                .filter(value -> value != null).count();
        if (filterCount > 1) {
            throw new BusinessRuleException("Only one task filter can be used at a time");
        }
        List<Task> tasks = personalOwnerId != null ? taskRepository.findByPersonalOwnerIdAndDeletedAtIsNull(personalOwnerId)
                : projectId != null ? taskRepository.findByProjectIdAndDeletedAtIsNull(projectId)
                : responsibleUserId != null ? taskRepository.findByResponsibleUserIdAndDeletedAtIsNull(responsibleUserId)
                : parentTaskId != null ? taskRepository.findByParentTaskIdAndDeletedAtIsNull(parentTaskId)
                : taskRepository.findByDeletedAtIsNull();
        return tasks.stream().map(this::toResponse).toList();
    }

    /** Replaces editable task data while preventing self-parenting. */
    @Transactional
    public TaskResponse update(UUID id, UpdateTaskRequest request) {
        validateContext(request.personalOwnerId(), request.projectId());
        Task task = requireActive(id);
        Task previousParent = task.getParentTask();
        validateParentHierarchy(id, request.parentTaskId());
        apply(task, request.title(), request.description(), request.notes(), request.personalOwnerId(),
                request.projectId(), request.responsibleUserId(), request.parentTaskId(), request.categoryId(),
                request.statusId(), request.deadline(), request.estimatedTimeSeconds(), request.pokerPoints());
        task.setUpdatedAt(Instant.now());
        Task savedTask = taskRepository.save(task);
        synchronizeAncestors(previousParent, savedTask.getUpdatedAt());
        if (savedTask.getParentTask() != previousParent) {
            synchronizeAncestors(savedTask.getParentTask(), savedTask.getUpdatedAt());
        }
        return toResponse(savedTask);
    }

    /**
     * Completes or reopens a task and recalculates every ancestor. A parent is
     * complete only when it has active children and all of them are complete.
     */
    @Transactional
    public TaskResponse updateCompletion(UUID id, boolean completed) {
        Task task = requireActive(id);
        Instant now = Instant.now();
        task.setCompletedAt(completed ? now : null);
        task.setUpdatedAt(now);
        Task savedTask = taskRepository.save(task);
        synchronizeAncestors(savedTask.getParentTask(), now);
        return toResponse(savedTask);
    }

    /** Soft-deletes a task by setting its deletion and update timestamps. */
    @Transactional
    public void delete(UUID id) {
        Task task = requireActive(id);
        Task parent = task.getParentTask();
        task.setDeletedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        taskRepository.save(task);
        synchronizeAncestors(parent, task.getUpdatedAt());
    }

    /** Resolves an active task and hides soft-deleted rows from normal operations. */
    public Task requireActive(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
        if (task.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Task not found: " + id);
        }
        return task;
    }

    /** Resolves request identifiers and applies scalar and relationship fields. */
    private void apply(Task task, String title, String description, String notes, UUID personalOwnerId,
                       UUID projectId, UUID responsibleUserId, UUID parentTaskId, UUID categoryId, UUID statusId,
                       java.time.LocalDate deadline, Long estimatedTimeSeconds, Integer pokerPoints) {
        task.setTitle(title);
        task.setDescription(description);
        task.setNotes(notes);
        task.setPersonalOwner(personalOwnerId == null ? null : userService.requireUser(personalOwnerId));
        task.setProject(projectId == null ? null : projectService.requireActive(projectId));
        task.setResponsibleUser(userService.requireUser(responsibleUserId));
        task.setParentTask(parentTaskId == null ? null : requireActive(parentTaskId));
        task.setCategory(categoryId == null ? null : categoryService.requireActive(categoryId));
        task.setStatus(taskStatusService.requireStatus(statusId));
        task.setDeadline(deadline);
        task.setEstimatedTimeSeconds(estimatedTimeSeconds);
        task.setPokerPoints(pokerPoints);
    }

    /** Enforces that exactly one of personalOwnerId and projectId is present. */
    private void validateContext(UUID personalOwnerId, UUID projectId) {
        if ((personalOwnerId == null) == (projectId == null)) {
            throw new BusinessRuleException("Task must belong to exactly one context: personal owner or project");
        }
    }

    /** Prevents direct or indirect cycles before changing a task parent. */
    private void validateParentHierarchy(UUID taskId, UUID parentTaskId) {
        if (parentTaskId == null) {
            return;
        }
        Set<UUID> visited = new HashSet<>();
        Task current = requireActive(parentTaskId);
        while (current != null && visited.add(current.getId())) {
            if (taskId.equals(current.getId())) {
                throw new BusinessRuleException("A task cannot be its own ancestor");
            }
            current = current.getParentTask();
        }
        if (current != null) {
            throw new BusinessRuleException("Task hierarchy contains a cycle");
        }
    }

    /** Recalculates completion from the direct active children and propagates it to the root. */
    private void synchronizeAncestors(Task parent, Instant now) {
        Task current = parent;
        Set<UUID> visited = new HashSet<>();
        while (current != null && visited.add(current.getId())) {
            List<Task> children = taskRepository.findByParentTaskIdAndDeletedAtIsNull(current.getId());
            boolean shouldBeCompleted = !children.isEmpty()
                    && children.stream().allMatch(child -> child.getCompletedAt() != null);
            boolean isCompleted = current.getCompletedAt() != null;
            if (shouldBeCompleted != isCompleted) {
                current.setCompletedAt(shouldBeCompleted ? now : null);
                current.setUpdatedAt(now);
                taskRepository.save(current);
            }
            current = current.getParentTask();
        }
    }

    /** Produces a stable REST representation without serializing JPA relationships. */
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getNotes(),
                task.getPersonalOwner() == null ? null : task.getPersonalOwner().getId(),
                task.getProject() == null ? null : task.getProject().getId(), task.getResponsibleUser().getId(),
                task.getParentTask() == null ? null : task.getParentTask().getId(),
                task.getCategory() == null ? null : task.getCategory().getId(), task.getStatus().getId(),
                task.getDeadline(), task.getEstimatedTimeSeconds(), task.getPokerPoints(), task.getCreatedAt(),
                task.getCompletedAt() != null, task.getCompletedAt(), task.getUpdatedAt());
    }
}
