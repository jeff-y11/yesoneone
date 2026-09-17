package com.geodevai.controller;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.PropertyRepository;
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
@RequestMapping("/services/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProperties(
            @RequestParam(required = false, defaultValue = "") String search) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        if (user.getOrganization() == null) {
            return ResponseEntity.status(403).build();
        }

        Organization org = user.getOrganization();
        List<Property> properties;

        if (search.isBlank()) {
            properties = propertyRepository.findByOrganization(org);
        } else {
            properties = propertyRepository.searchByOrganizationAndQuery(org, search);
        }

        return ResponseEntity.ok(Map.of(
                "items", properties,
                "count", properties.size(),
                "query", search
        ));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<Map<String, Object>> getProperty(@PathVariable UUID propertyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        if (user.getOrganization() == null) {
            return ResponseEntity.status(403).build();
        }

        return propertyRepository.findById(propertyId)
                .filter(property -> property.getOrganization().getOrganizationId()
                        .equals(user.getOrganization().getOrganizationId()))
                .map(property -> ResponseEntity.ok((Map<String, Object>) Map.of(
                        "propertyId", property.getPropertyId(),
                        "name", property.getName(),
                        "externalPropertyId", property.getExternalPropertyId(),
                        "propertyType", property.getPropertyType() != null ? property.getPropertyType() : "",
                        "numberOfUnits", property.getNumberOfUnits() != null ? property.getNumberOfUnits() : 0,
                        "managementCompany", property.getManagementCompany() != null ? property.getManagementCompany() : "",
                        "address", property.getAddress() != null ? property.getAddress() : Map.of(),
                        "organizationId", property.getOrganization().getOrganizationId(),
                        "lastSyncTime", property.getLastSyncTime() != null ? property.getLastSyncTime() : ""
                )))
                .orElse(ResponseEntity.status(403).build());
    }
}
