package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, UUID> {

    List<TaskStatus> findByOwnerIdOrderByPosition(UUID ownerId);

    List<TaskStatus> findByOwnerIsNullOrderByPosition();
}
