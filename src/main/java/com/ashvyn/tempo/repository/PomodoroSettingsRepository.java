package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.PomodoroSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PomodoroSettingsRepository extends JpaRepository<PomodoroSettings, UUID> {
}
