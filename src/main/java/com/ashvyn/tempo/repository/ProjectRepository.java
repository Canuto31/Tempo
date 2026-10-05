package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByDeletedAtIsNull();

    List<Project> findByOwnerIdAndDeletedAtIsNull(UUID ownerId);
}
