package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByDeletedAtIsNull();

    List<Category> findByOwnerIdAndDeletedAtIsNull(UUID ownerId);

    List<Category> findByOwnerIsNullAndDeletedAtIsNull();
}
