package com.ashvyn.tempo.repository;

import com.ashvyn.tempo.entity.RecurrenceRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecurrenceRuleRepository extends JpaRepository<RecurrenceRule, UUID> {
}
