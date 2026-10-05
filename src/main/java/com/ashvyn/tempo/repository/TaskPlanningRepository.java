package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.TaskPlanning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskPlanningRepository extends JpaRepository<TaskPlanning, UUID> {
}
