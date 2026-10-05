package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LabelRepository extends JpaRepository<Label, UUID> {

    List<Label> findByOwnerIdAndDeletedAtIsNull(UUID ownerId);

    List<Label> findByOwnerIsNullAndDeletedAtIsNull();
}
