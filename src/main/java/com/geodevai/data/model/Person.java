package com.geodevai.data.model;

import com.geodevai.data.model.Externalable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Person extends AuditableEntity implements Externalable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID personId;
    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String externalTenantId;
    private String phoneNumber;
    private String email;
    private LocalDateTime leaseStartDate;
    private LocalDateTime leaseEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_id")
    private Integration integration;

    @Transient
    private UUID integrationId;
    private LocalDateTime lastSyncTime;

    @Override
    public UUID getIntegrationId() { return integration != null ? integration.getIntegrationId() : null; }
    @Override
    public void setIntegrationId(UUID integrationId) { this.integrationId = integrationId; }
    @Override
    public String getIntegrationRemoteId() { return externalTenantId; }
    @Override
    public void setIntegrationRemoteId(String integrationRemoteId) { this.externalTenantId = integrationRemoteId; }
    @Override
    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    @Override
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
