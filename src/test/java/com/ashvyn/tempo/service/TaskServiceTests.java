package com.ashvyn.tempo.service;

import com.ashvyn.tempo.dto.task.CreateTaskRequest;
import com.ashvyn.tempo.entity.Task;
import com.ashvyn.tempo.exception.BusinessRuleException;
import com.ashvyn.tempo.exception.ResourceNotFoundException;
import com.ashvyn.tempo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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

    private CreateTaskRequest request(UUID personalOwnerId, UUID projectId) {
        return new CreateTaskRequest("Task", null, null, personalOwnerId, projectId, UUID.randomUUID(), null,
                null, UUID.randomUUID(), null, 0L, 0);
    }
}
