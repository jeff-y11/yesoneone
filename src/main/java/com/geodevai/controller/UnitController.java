package com.geodevai.controller;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import com.geodevai.data.model.Unit;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.PropertyRepository;
import com.geodevai.data.repository.UnitRepository;
import com.geodevai.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/services/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUnits(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) UUID propertyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        if (user.getOrganization() == null) {
            return ResponseEntity.status(403).build();
        }

        Organization org = user.getOrganization();
        List<Unit> units;

        if (propertyId != null) {
            return propertyRepository.findById(propertyId)
                    .filter(property -> property.getOrganization().getOrganizationId()
                            .equals(org.getOrganizationId()))
                    .map(property -> {
                        List<Unit> propertyUnits = unitRepository.findByProperty(property);
                        return ResponseEntity.ok((Map<String, Object>) Map.of(
                                "items", propertyUnits,
                                "count", propertyUnits.size(),
                                "query", search
                        ));
                    })
                    .orElse(ResponseEntity.status(403).build());
        }

        if (search.isBlank()) {
            units = unitRepository.findByOrganization(org);
        } else {
            units = unitRepository.searchByOrganizationAndQuery(org, search);
        }

        return ResponseEntity.ok(Map.of(
                "items", units,
                "count", units.size(),
                "query", search
        ));
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<Map<String, Object>> getUnit(@PathVariable UUID unitId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        if (user.getOrganization() == null) {
            return ResponseEntity.status(403).build();
        }

        return unitRepository.findById(unitId)
                .filter(unit -> unit.getProperty().getOrganization().getOrganizationId()
                        .equals(user.getOrganization().getOrganizationId()))
                .map(unit -> ResponseEntity.ok((Map<String, Object>) Map.of(
                        "unitId", unit.getUnitId(),
                        "unitNumber", unit.getUnitNumber(),
                        "externalUnitId", unit.getExternalUnitId(),
                        "unitType", unit.getUnitType() != null ? unit.getUnitType() : "",
                        "squareFootage", unit.getSquareFootage() != null ? unit.getSquareFootage() : 0,
                        "bedrooms", unit.getBedrooms() != null ? unit.getBedrooms() : 0,
                        "bathrooms", unit.getBathrooms() != null ? unit.getBathrooms() : 0,
                        "rentAmount", unit.getRentAmount() != null ? unit.getRentAmount() : 0,
                        "isOccupied", unit.getIsOccupied() != null ? unit.getIsOccupied() : false,
                        "propertyId", unit.getProperty().getPropertyId(),
                        "propertyName", unit.getProperty().getName(),
                        "lastSyncTime", unit.getLastSyncTime() != null ? unit.getLastSyncTime() : ""
                )))
                .orElse(ResponseEntity.status(403).build());
    }
}
