package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.WorkSessionPause;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkSessionPauseRepository extends JpaRepository<WorkSessionPause, UUID> {
}
