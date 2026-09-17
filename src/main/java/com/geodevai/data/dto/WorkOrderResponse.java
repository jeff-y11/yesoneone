package com.geodevai.data.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkOrderResponse(
    UUID workOrderId,
    String externalWorkOrderId,
    String title,
    String summary,
    String status,
    String priority,
    BigDecimal amount,
    LocalDateTime dueDate,
    LocalDateTime completionDate,
    UUID propertyId,
    String propertyName,
    UUID unitId,
    String unitNumber,
    UUID tenantId,
    String tenantName,
    String callSource,
    String callerName,
    String callerContactInfo,
    LocalDateTime lastSyncTime
) {}
