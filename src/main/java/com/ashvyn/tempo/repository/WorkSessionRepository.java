package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkSessionRepository extends JpaRepository<WorkSession, UUID> {
}
