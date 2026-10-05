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
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create project")
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get active project")
    public ProjectResponse get(@PathVariable UUID id) {
        return projectService.get(id);
    }

    @GetMapping
    @Operation(summary = "List active projects")
    public List<ProjectResponse> list(@RequestParam(required = false) UUID ownerId) {
        return projectService.list(ownerId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project")
    public ProjectResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateProjectRequest request) {
        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete project")
    public void delete(@PathVariable UUID id) {
        projectService.delete(id);
    }
}
