package com.ashvyn.tempo.service;

import com.ashvyn.tempo.dto.category.CategoryResponse;
import com.ashvyn.tempo.dto.category.CreateCategoryRequest;
import com.ashvyn.tempo.dto.category.UpdateCategoryRequest;
import com.ashvyn.tempo.entity.Category;
import com.ashvyn.tempo.exception.ResourceNotFoundException;
import com.ashvyn.tempo.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        Instant now = Instant.now();
        Category category = new Category();
        category.setOwner(request.ownerId() == null ? null : userService.requireUser(request.ownerId()));
        category.setName(request.name());
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(UUID id) {
        return toResponse(requireActive(id));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(UUID ownerId, boolean global) {
        List<Category> categories = global ? categoryRepository.findByOwnerIsNullAndDeletedAtIsNull()
                : ownerId == null ? categoryRepository.findByDeletedAtIsNull()
                : categoryRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
        return categories.stream().map(this::toResponse).toList();
    }

    @Transactional
    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {
        Category category = requireActive(id);
        category.setName(request.name());
        category.setUpdatedAt(Instant.now());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID id) {
        Category category = requireActive(id);
        category.setDeletedAt(Instant.now());
        category.setUpdatedAt(Instant.now());
        categoryRepository.save(category);
    }

    public Category requireActive(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        if (category.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        return category;
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getOwner() == null ? null : category.getOwner().getId(),
                category.getName(), category.getCreatedAt(), category.getUpdatedAt());
    }
}
