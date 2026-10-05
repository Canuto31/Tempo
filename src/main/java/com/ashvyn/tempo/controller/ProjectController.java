package com.ashvyn.tempo.controller;

import com.ashvyn.tempo.dto.project.CreateProjectRequest;
import com.ashvyn.tempo.dto.project.ProjectResponse;
import com.ashvyn.tempo.dto.project.UpdateProjectRequest;
import com.ashvyn.tempo.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Project management endpoints")
/** Exposes CRUD operations for active projects. */
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /** Creates a project for an existing owner and optional parent project. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create project")
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }

    /** Returns an active project or responds with 404. */
    @GetMapping("/{id}")
    @Operation(summary = "Get active project")
    public ProjectResponse get(@PathVariable UUID id) {
        return projectService.get(id);
    }

    /** Lists active projects, optionally restricted to one owner. */
    @GetMapping
    @Operation(summary = "List active projects")
    public List<ProjectResponse> list(@RequestParam(required = false) UUID ownerId) {
        return projectService.list(ownerId);
    }

    /** Replaces the editable data of an active project. */
    @PutMapping("/{id}")
    @Operation(summary = "Update project")
    public ProjectResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateProjectRequest request) {
        return projectService.update(id, request);
    }

    /** Marks a project as deleted without removing its database row. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete project")
    public void delete(@PathVariable UUID id) {
        projectService.delete(id);
    }
}
