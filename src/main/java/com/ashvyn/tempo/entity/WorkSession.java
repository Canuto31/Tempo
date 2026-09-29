package com.ashvyn.tempo.entity;

import com.ashvyn.tempo.enums.WorkSessionStatus;
import com.ashvyn.tempo.enums.WorkSessionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "work_sessions")
@Data
public class WorkSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkSessionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkSessionStatus status;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant endedAt;

    @Column(nullable = false)
    private Long accumulatedTimeSeconds;

    @Version
    private Long version;
}
