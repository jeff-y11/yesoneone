package com.geodevai.controller;

import com.geodevai.data.dto.PropertyResponse;
import com.geodevai.data.dto.SearchResponse;
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
    public ResponseEntity<SearchResponse<PropertyResponse>> getProperties(
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

        List<PropertyResponse> items = properties.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new SearchResponse<>(items, items.size(), search));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> getProperty(@PathVariable UUID propertyId) {
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
                .map(property -> ResponseEntity.ok(toResponse(property)))
                .orElse(ResponseEntity.status(403).build());
    }

    private PropertyResponse toResponse(Property property) {
        Map<String, Object> address = property.getAddress() != null
                ? Map.of(
                    "addressId", property.getAddress().getAddressId(),
                    "addressLine1", property.getAddress().getAddressLine1() != null ? property.getAddress().getAddressLine1() : "",
                    "addressLine2", property.getAddress().getAddressLine2() != null ? property.getAddress().getAddressLine2() : "",
                    "city", property.getAddress().getCity() != null ? property.getAddress().getCity() : "",
                    "state", property.getAddress().getState() != null ? property.getAddress().getState() : "",
                    "postalCode", property.getAddress().getPostalCode() != null ? property.getAddress().getPostalCode() : "",
                    "country", property.getAddress().getCountry() != null ? property.getAddress().getCountry() : ""
                )
                : Map.of();

        return new PropertyResponse(
                property.getPropertyId(),
                property.getName(),
                property.getExternalPropertyId(),
                property.getPropertyType(),
                property.getNumberOfUnits(),
                property.getManagementCompany(),
                address,
                property.getOrganization().getOrganizationId(),
                property.getLastSyncTime()
        );
    }
}
