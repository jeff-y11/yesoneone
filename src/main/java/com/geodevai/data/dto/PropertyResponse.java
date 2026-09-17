package com.geodevai.data.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record PropertyResponse(
    UUID propertyId,
    String name,
    String externalPropertyId,
    String propertyType,
    Integer numberOfUnits,
    String managementCompany,
    Map<String, Object> address,
    UUID organizationId,
    LocalDateTime lastSyncTime
) {}
