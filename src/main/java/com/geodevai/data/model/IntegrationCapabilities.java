package com.geodevai.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "integration_capability")
@Getter
@Setter
@NoArgsConstructor
public class IntegrationCapabilities extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID capabilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_id", nullable = false)
    private Integration integration;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private boolean required;
}
