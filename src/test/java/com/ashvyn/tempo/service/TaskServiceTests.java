package com.ashvyn.tempo.service;

import com.ashvyn.tempo.dto.task.CreateTaskRequest;
import com.ashvyn.tempo.entity.Task;
import com.ashvyn.tempo.entity.TaskStatus;
import com.ashvyn.tempo.entity.User;
import com.ashvyn.tempo.exception.BusinessRuleException;
import com.ashvyn.tempo.exception.ResourceNotFoundException;
import com.ashvyn.tempo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserService userService;
    @Mock
    private ProjectService projectService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private TaskStatusService taskStatusService;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, userService, projectService, categoryService, taskStatusService);
    }

    @Test
    void createRejectsTaskWithoutAContext() {
        CreateTaskRequest request = request(null, null);

        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("exactly one context");
    }

    @Test
    void createRejectsTaskWithBothContexts() {
        CreateTaskRequest request = request(UUID.randomUUID(), UUID.randomUUID());

        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("exactly one context");
    }

    @Test
    void listRejectsMoreThanOneFilter() {
        assertThatThrownBy(() -> taskService.list(UUID.randomUUID(), UUID.randomUUID(), null, null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Only one task filter");
    }

    @Test
    void getReturnsNotFoundForMissingTask() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.get(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteUsesSoftDelete() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        task.setId(id);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        taskService.delete(id);

        assertThat(task.getDeletedAt()).isNotNull();
        assertThat(task.getUpdatedAt()).isNotNull();
        verify(taskRepository).save(task);
    }

    @Test
    void completingTheLastPendingChildCompletesItsParent() {
        Task parent = taskWithRequiredRelationships(UUID.randomUUID());
        Task child = taskWithRequiredRelationships(UUID.randomUUID());
        child.setParentTask(parent);
        when(taskRepository.findById(child.getId())).thenReturn(Optional.of(child));
        when(taskRepository.save(child)).thenReturn(child);
        when(taskRepository.findByParentTaskIdAndDeletedAtIsNull(parent.getId())).thenReturn(List.of(child));

        taskService.updateCompletion(child.getId(), true);

        assertThat(child.getCompletedAt()).isNotNull();
        assertThat(parent.getCompletedAt()).isNotNull();
        verify(taskRepository).save(parent);
    }

    @Test
    void reopeningAChildReopensItsCompletedParent() {
        Task parent = taskWithRequiredRelationships(UUID.randomUUID());
        parent.setCompletedAt(java.time.Instant.now());
        Task child = taskWithRequiredRelationships(UUID.randomUUID());
        child.setCompletedAt(java.time.Instant.now());
        child.setParentTask(parent);
        when(taskRepository.findById(child.getId())).thenReturn(Optional.of(child));
        when(taskRepository.save(child)).thenReturn(child);
        when(taskRepository.findByParentTaskIdAndDeletedAtIsNull(parent.getId())).thenReturn(List.of(child));

        taskService.updateCompletion(child.getId(), false);

        assertThat(child.getCompletedAt()).isNull();
        assertThat(parent.getCompletedAt()).isNull();
        verify(taskRepository).save(parent);
    }

    private CreateTaskRequest request(UUID personalOwnerId, UUID projectId) {
        return new CreateTaskRequest("Task", null, null, personalOwnerId, projectId, UUID.randomUUID(), null,
                null, UUID.randomUUID(), null, 0L, 0);
    }

    private Task taskWithRequiredRelationships(UUID id) {
        User responsible = new User();
        responsible.setId(UUID.randomUUID());
        TaskStatus status = new TaskStatus();
        status.setId(UUID.randomUUID());
        Task task = new Task();
        task.setId(id);
        task.setResponsibleUser(responsible);
        task.setStatus(status);
        return task;
    }
}
