package com.ashvyn.tempo.controller;

import com.ashvyn.tempo.dto.category.CategoryResponse;
import com.ashvyn.tempo.dto.category.CreateCategoryRequest;
import com.ashvyn.tempo.dto.category.UpdateCategoryRequest;
import com.ashvyn.tempo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Global and user category endpoints")
/** Exposes active global and user-owned categories. */
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** Creates a global category when ownerId is absent, otherwise a user category. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create category")
    public CategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {
        return categoryService.create(request);
    }

    /** Returns an active category by identifier. */
    @GetMapping("/{id}")
    @Operation(summary = "Get active category")
    public CategoryResponse get(@PathVariable UUID id) {
        return categoryService.get(id);
    }

    /** Lists all active categories or filters them by owner/global scope. */
    @GetMapping
    @Operation(summary = "List active categories")
    public List<CategoryResponse> list(@RequestParam(required = false) UUID ownerId,
                                       @RequestParam(defaultValue = "false") boolean global) {
        return categoryService.list(ownerId, global);
    }

    /** Updates the name of an active category. */
    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public CategoryResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateCategoryRequest request) {
        return categoryService.update(id, request);
    }

    /** Marks a category as deleted without removing its row. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete category")
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }
}
