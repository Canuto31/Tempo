package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.TaskPostponement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskPostponementRepository extends JpaRepository<TaskPostponement, UUID> {
}
