package com.ashvyn.tempo.config;

import com.ashvyn.tempo.entity.*;
import com.ashvyn.tempo.enums.AuthProvider;
import com.ashvyn.tempo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

/**
 * Creates a small, coherent Sprint 1 dataset for local development.
 *
 * <p>The initializer only runs with the {@code dev} profile, when
 * {@code tempo.seed.enabled=true}, and when the users table is empty. It never
 * replaces or deletes existing information.</p>
 */
@Component
@Profile("dev")
@ConditionalOnProperty(name = "tempo.seed.enabled", havingValue = "true")
public class DevDataInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevDataInitializer.class);

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final LabelRepository labelRepository;
    private final TaskLabelRepository taskLabelRepository;
    private final TaskStatusRepository taskStatusRepository;

    public DevDataInitializer(UserRepository userRepository, ProjectRepository projectRepository,
                              TaskRepository taskRepository, CategoryRepository categoryRepository,
                              LabelRepository labelRepository, TaskLabelRepository taskLabelRepository,
                              TaskStatusRepository taskStatusRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.labelRepository = labelRepository;
        this.taskLabelRepository = taskLabelRepository;
        this.taskStatusRepository = taskStatusRepository;
    }

    /**
     * Seeds related records in dependency order so every foreign key points to
     * a previously persisted entity.
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            LOGGER.info("Development seed skipped because user data already exists");
            return;
        }

        Date userNow = new Date();
        Instant now = Instant.now();

        User ana = user("Ana Torres", "ana", "ana@tempo.local", userNow);
        User mateo = user("Mateo Ruiz", "mateo", "mateo@tempo.local", userNow);
        userRepository.saveAll(java.util.List.of(ana, mateo));

        TaskStatus pending = status(null, "Pending", 1, true, now);
        TaskStatus inProgress = status(null, "In progress", 2, false, now);
        TaskStatus completed = status(null, "Completed", 3, false, now);
        TaskStatus blocked = status(ana, "Blocked", 4, false, now);
        taskStatusRepository.saveAll(java.util.List.of(pending, inProgress, completed, blocked));

        Category work = category(null, "Work", now);
        Category personal = category(null, "Personal", now);
        Category deepWork = category(ana, "Deep work", now);
        categoryRepository.saveAll(java.util.List.of(work, personal, deepWork));

        Label urgent = label(null, "Urgent", now);
        Label focus = label(null, "Focus", now);
        Label backend = label(ana, "Backend", now);
        labelRepository.saveAll(java.util.List.of(urgent, focus, backend));

        Project tempoMvp = project(ana, null, "Tempo MVP",
                "First functional version of the Tempo task management API.", LocalDate.now().plusWeeks(4), now);
        Project api = project(ana, tempoMvp, "REST API",
                "Backend endpoints, validation and OpenAPI documentation.", LocalDate.now().plusWeeks(2), now);
        projectRepository.save(tempoMvp);
        projectRepository.save(api);

        Task reviewBacklog = task("Review personal backlog", "Organize pending personal tasks.", ana, null,
                ana, null, personal, pending, LocalDate.now().plusDays(2), 1800L, 2, now);
        Task documentApi = task("Review API documentation", "Validate every endpoint in Swagger UI.", null, api,
                ana, null, work, inProgress, LocalDate.now().plusDays(3), 3600L, 3, now);
        Task testTasks = task("Test task endpoints", "Run create, update, list and soft-delete scenarios.", null, api,
                mateo, documentApi, deepWork, pending, LocalDate.now().plusDays(5), 5400L, 5, now);
        taskRepository.saveAll(java.util.List.of(reviewBacklog, documentApi, testTasks));

        taskLabelRepository.save(taskLabel(documentApi, focus));
        taskLabelRepository.save(taskLabel(documentApi, backend));
        taskLabelRepository.save(taskLabel(testTasks, urgent));

        LOGGER.info("Development seed created: 2 users, 2 projects, 3 tasks, 3 categories, 3 labels and 4 statuses");
    }

    private User user(String name, String username, String email, Date now) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("tempo-demo");
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    private TaskStatus status(User owner, String name, int position, boolean defaultStatus, Instant now) {
        TaskStatus status = new TaskStatus();
        status.setOwner(owner);
        status.setName(name);
        status.setPosition(position);
        status.setDefaultStatus(defaultStatus);
        status.setCreatedAt(now);
        status.setUpdatedAt(now);
        return status;
    }

    private Category category(User owner, String name, Instant now) {
        Category category = new Category();
        category.setOwner(owner);
        category.setName(name);
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        return category;
    }

    private Label label(User owner, String name, Instant now) {
        Label label = new Label();
        label.setOwner(owner);
        label.setName(name);
        label.setCreatedAt(now);
        label.setUpdatedAt(now);
        return label;
    }

    private Project project(User owner, Project parent, String name, String description, LocalDate deadline,
                            Instant now) {
        Project project = new Project();
        project.setOwner(owner);
        project.setParentProject(parent);
        project.setName(name);
        project.setDescription(description);
        project.setDeadline(deadline);
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
        return project;
    }

    private Task task(String title, String description, User personalOwner, Project project, User responsible,
                      Task parent, Category category, TaskStatus status, LocalDate deadline,
                      long estimatedSeconds, int pokerPoints, Instant now) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPersonalOwner(personalOwner);
        task.setProject(project);
        task.setResponsibleUser(responsible);
        task.setParentTask(parent);
        task.setCategory(category);
        task.setStatus(status);
        task.setDeadline(deadline);
        task.setEstimatedTimeSeconds(estimatedSeconds);
        task.setPokerPoints(pokerPoints);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        return task;
    }

    private TaskLabel taskLabel(Task task, Label label) {
        TaskLabel taskLabel = new TaskLabel();
        taskLabel.setTask(task);
        taskLabel.setLabel(label);
        return taskLabel;
    }
}
