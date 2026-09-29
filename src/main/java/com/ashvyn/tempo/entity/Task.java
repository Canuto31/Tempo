package com.ashvyn.tempo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 5000)
    private String description;

    @Column(length = 10000)
    private String notes;

    /*
     * Contexto personal.
     * NULL cuando la tarea pertenece a un Project.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_owner_id")
    private User personalOwner;

    /*
     * Contexto de proyecto.
     * NULL cuando la tarea es personal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    /*
     * Usuario responsable de ejecutar la tarea.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsible_user_id", nullable = false)
    private User responsibleUser;

    /*
     * Jerarquía de tareas.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private TaskStatus status;

    /*
     * Fecha límite real.
     */
    private LocalDate deadline;

    /*
     * Tiempo total estimado.
     * Unidad: segundos.
     */
    @Column(nullable = false)
    private Long estimatedTimeSeconds;

    @Column(nullable = false)
    private Integer pokerPoints;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant completedAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant deletedAt;

    @Version
    private Long version;
}
