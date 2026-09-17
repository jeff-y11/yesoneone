package com.geodevai.data.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UnitResponse(
    UUID unitId,
    String unitNumber,
    String externalUnitId,
    String unitType,
    Integer squareFootage,
    Integer bedrooms,
    Integer bathrooms,
    BigDecimal rentAmount,
    Boolean isOccupied,
    UUID propertyId,
    String propertyName,
    LocalDateTime lastSyncTime
) {}
