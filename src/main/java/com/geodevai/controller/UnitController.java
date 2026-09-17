package com.geodevai.controller;

import com.geodevai.data.dto.SearchResponse;
import com.geodevai.data.dto.UnitResponse;
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
import java.util.UUID;

@RestController
@RequestMapping("/services/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<SearchResponse<UnitResponse>> getUnits(
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
                        List<UnitResponse> items = propertyUnits.stream()
                                .map(this::toResponse)
                                .toList();
                        return ResponseEntity.ok(new SearchResponse<>(items, items.size(), search));
                    })
                    .orElse(ResponseEntity.status(403).build());
        }

        if (search.isBlank()) {
            units = unitRepository.findByOrganization(org);
        } else {
            units = unitRepository.searchByOrganizationAndQuery(org, search);
        }

        List<UnitResponse> items = units.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new SearchResponse<>(items, items.size(), search));
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<UnitResponse> getUnit(@PathVariable UUID unitId) {
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
                .map(unit -> ResponseEntity.ok(toResponse(unit)))
                .orElse(ResponseEntity.status(403).build());
    }

    private UnitResponse toResponse(Unit unit) {
        return new UnitResponse(
                unit.getUnitId(),
                unit.getUnitNumber(),
                unit.getExternalUnitId(),
                unit.getUnitType(),
                unit.getSquareFootage(),
                unit.getBedrooms(),
                unit.getBathrooms(),
                unit.getRentAmount(),
                unit.getIsOccupied(),
                unit.getProperty().getPropertyId(),
                unit.getProperty().getName(),
                unit.getLastSyncTime()
        );
    }
}
