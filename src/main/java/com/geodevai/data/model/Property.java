package com.geodevai.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "property")
@Getter
@Setter
@NoArgsConstructor
public class Property extends AuditableEntity implements Externalable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID propertyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String externalPropertyId;

    private String propertyType;

    private Integer numberOfUnits;

    private String managementCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Transient
    private UUID integrationId;

    private LocalDateTime lastSyncTime;

    @Override
    public UUID getIntegrationId() { return integrationId; }
    @Override
    public void setIntegrationId(UUID integrationId) { this.integrationId = integrationId; }
    @Override
    public String getIntegrationRemoteId() { return externalPropertyId; }
    @Override
    public void setIntegrationRemoteId(String integrationRemoteId) { this.externalPropertyId = integrationRemoteId; }
    @Override
    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    @Override
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
