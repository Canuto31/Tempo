package com.ashvyn.tempo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "pomodoro_settings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_pomodoro_settings_user", columnNames = "user_id")
})
@Data
public class PomodoroSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;
    @Column(nullable = false)
    private Integer workDuration;
    @Column(nullable = false)
    private Integer shortBreakDuration;
    @Column(nullable = false)
    private Integer longBreakDuration;
    @Column(nullable = false)
    private Integer pomodoroUntilLongBreak;

    @Version
    private Long version;
}
