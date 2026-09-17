package com.geodevai.data.model;

import com.geodevai.data.model.Externalable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_order")
@Getter
@Setter
@NoArgsConstructor
public class WorkOrder extends AuditableEntity implements Externalable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID workOrderId;

    @Column(nullable = false)
    private String externalWorkOrderId;

    @Column(nullable = false)
    private String title;

    private String summary;

    private String description;

    private String workDetails;

    @Column(nullable = false)
    private String status;

    private String priority;

    private String workOrderType;

    private BigDecimal amount;

    private LocalDateTime dueDate;

    private LocalDateTime completionDate;

    private String entryNotes;

    private String vendorNotes;

    private String invoiceNumber;

    private String chargeableTo;

    private String callSource;

    private String callerName;

    private String callerContactInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Person tenant;

    private String assignedTo;

    private String vendorId;

    @Transient
    private UUID integrationId;

    private LocalDateTime lastSyncTime;

    @Override
    public UUID getIntegrationId() { return integrationId; }
    @Override
    public void setIntegrationId(UUID integrationId) { this.integrationId = integrationId; }
    @Override
    public String getIntegrationRemoteId() { return externalWorkOrderId; }
    @Override
    public void setIntegrationRemoteId(String integrationRemoteId) { this.externalWorkOrderId = integrationRemoteId; }
    @Override
    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    @Override
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
