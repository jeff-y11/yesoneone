package com.geodevai.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "integration_schedule")
@Getter
@Setter
@NoArgsConstructor
public class IntegrationSchedule extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_id", nullable = false)
    private Integration integration;

    @Column(nullable = false)
    private String cronExpression;

    private Boolean active = true;

    private LocalDateTime lastRunTime;

    private LocalDateTime nextRunTime;

    private Integer maxRetries = 3;

    private Integer currentRetryCount = 0;
}
