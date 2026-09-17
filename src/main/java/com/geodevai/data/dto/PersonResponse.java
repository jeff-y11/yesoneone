package com.geodevai.data.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PersonResponse(
    UUID personId,
    String firstName,
    String lastName,
    String externalTenantId,
    String phoneNumber,
    String email,
    LocalDateTime leaseStartDate,
    LocalDateTime leaseEndDate,
    UUID unitId,
    String unitNumber,
    UUID integrationId,
    LocalDateTime lastSyncTime
) {}
