package com.ashvyn.tempo.service;

import com.ashvyn.tempo.dto.project.CreateProjectRequest;
import com.ashvyn.tempo.dto.project.ProjectResponse;
import com.ashvyn.tempo.dto.project.UpdateProjectRequest;
import com.ashvyn.tempo.entity.Project;
import com.ashvyn.tempo.exception.BusinessRuleException;
import com.ashvyn.tempo.exception.ResourceNotFoundException;
import com.ashvyn.tempo.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        Instant now = Instant.now();
        Project project = new Project();
        project.setOwner(userService.requireUser(request.ownerId()));
        project.setParentProject(resolveParent(request.parentProjectId(), request.ownerId(), null));
        project.setName(request.name());
        project.setDescription(request.description());
        project.setDeadline(request.deadline());
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
        return toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public ProjectResponse get(UUID id) {
        return toResponse(requireActive(id));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> list(UUID ownerId) {
        List<Project> projects = ownerId == null
                ? projectRepository.findByDeletedAtIsNull()
                : projectRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
        return projects.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProjectResponse update(UUID id, UpdateProjectRequest request) {
        Project project = requireActive(id);
        project.setParentProject(resolveParent(request.parentProjectId(), project.getOwner().getId(), id));
        project.setName(request.name());
        project.setDescription(request.description());
        project.setDeadline(request.deadline());
        project.setUpdatedAt(Instant.now());
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(UUID id) {
        Project project = requireActive(id);
        project.setDeletedAt(Instant.now());
        project.setUpdatedAt(Instant.now());
        projectRepository.save(project);
    }

    public Project requireActive(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
        if (project.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Project not found: " + id);
        }
        return project;
    }

    private Project resolveParent(UUID parentId, UUID ownerId, UUID projectId) {
        if (parentId == null) {
            return null;
        }
        if (parentId.equals(projectId)) {
            throw new BusinessRuleException("A project cannot be its own parent");
        }
        Project parent = requireActive(parentId);
        if (!parent.getOwner().getId().equals(ownerId)) {
            throw new BusinessRuleException("Parent project must have the same owner");
        }
        return parent;
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(project.getId(), project.getOwner().getId(),
                project.getParentProject() == null ? null : project.getParentProject().getId(),
                project.getName(), project.getDescription(), project.getDeadline(), project.getCreatedAt(),
                project.getUpdatedAt());
    }
}
