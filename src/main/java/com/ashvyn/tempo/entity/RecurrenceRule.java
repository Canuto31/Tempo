package com.ashvyn.tempo.entity;

import com.ashvyn.tempo.enums.RecurrenceFrequency;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "recurrence_rules")
@Data
public class RecurrenceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false, unique = true)
    private Task task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecurrenceFrequency frequency;

    @Column(nullable = false)
    private Integer interval;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 50)
    private String daysOfWeek;

    private Integer dayOfMonth;

    @Column(nullable = false)
    private boolean active;
}
