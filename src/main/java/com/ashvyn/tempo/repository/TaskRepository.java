package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByPersonalOwnerIdAndDeletedAtIsNull(UUID userId);

    List<Task> findByProjectIdAndDeletedAtIsNull(UUID projectId);

    List<Task> findByResponsibleUserIdAndDeletedAtIsNull(UUID userId);

    List<Task> findByParentTaskIdAndDeletedAtIsNull(UUID parentTaskId);
}
