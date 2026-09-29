package com.ashvyn.tempo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(
        name = "task_labels",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_task_label",
                        columnNames = {"task_id", "label_id"}
                )
        }
)
@Data
public class TaskLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "label_id", nullable = false)
    private Label label;
}
