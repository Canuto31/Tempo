package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.TaskLabel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskLabelRepository extends JpaRepository<TaskLabel, UUID> {
}
