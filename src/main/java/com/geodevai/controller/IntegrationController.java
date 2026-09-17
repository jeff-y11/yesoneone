package com.geodevai.controller;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.Organization;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.IntegrationRepository;
import com.geodevai.data.repository.OrganizationRepository;
import com.geodevai.data.repository.UserRepository;
import com.geodevai.integration.IntegrationDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/services/integrations")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationRepository integrationRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final IntegrationDispatcher integrationDispatcher;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createIntegration(@RequestBody Map<String, Object> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        if (user.getOrganization() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User has no organization"));
        }

        Organization org = user.getOrganization();

        String name = (String) request.get("name");
        String endpoint = (String) request.get("endpoint");
        String type = (String) request.get("type");

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
        }
        if (endpoint == null || endpoint.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Endpoint is required"));
        }
        if (type == null || type.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Type is required"));
        }

        Integration integration = new Integration();
        integration.setName(name);
        integration.setEndpoint(endpoint);
        integration.setType(type);
        integration.setOrganization(org);

        @SuppressWarnings("unchecked")
        Map<String, String> parameters = (Map<String, String>) request.get("parameters");
        if (parameters != null) {
            integration.setParameters(parameters);
        }

        integrationRepository.save(integration);

        return ResponseEntity.ok(Map.of(
                "integrationId", integration.getIntegrationId(),
                "name", integration.getName(),
                "endpoint", integration.getEndpoint(),
                "type", integration.getType(),
                "parameters", integration.getParameters() != null ? integration.getParameters() : Map.of(),
                "active", integration.isActive()
        ));
    }

    @DeleteMapping("/{integrationId}")
    public ResponseEntity<Void> deleteIntegration(@PathVariable UUID integrationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        Integration integration = integrationRepository.findById(integrationId)
                .orElseThrow();

        if (user.getOrganization() == null ||
            !user.getOrganization().getOrganizationId().equals(integration.getOrganization().getOrganizationId())) {
            return ResponseEntity.status(403).build();
        }

        integration.setActive(false);
        integrationRepository.save(integration);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{integrationId}/run")
    public ResponseEntity<Map<String, Object>> runIntegration(@PathVariable UUID integrationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        Integration integration = integrationRepository.findById(integrationId)
                .orElseThrow();

        if (user.getOrganization() == null ||
            !user.getOrganization().getOrganizationId().equals(integration.getOrganization().getOrganizationId())) {
            return ResponseEntity.status(403).build();
        }

        if (!integration.isActive()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Integration is not active"));
        }

        integrationDispatcher.dispatch(integrationId, integrationRepository);

        return ResponseEntity.ok(Map.of(
            "integrationId", integration.getIntegrationId(),
            "status", "RUNNING",
            "message", "Integration run initiated"
        ));
    }

    @PostMapping("/{integrationId}/toggle-active")
    public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable UUID integrationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        Integration integration = integrationRepository.findById(integrationId)
                .orElseThrow();

        if (user.getOrganization() == null ||
            !user.getOrganization().getOrganizationId().equals(integration.getOrganization().getOrganizationId())) {
            return ResponseEntity.status(403).build();
        }

        integration.setActive(!integration.isActive());
        integrationRepository.save(integration);

        return ResponseEntity.ok(Map.of(
            "integrationId", integration.getIntegrationId(),
            "active", integration.isActive(),
            "message", integration.isActive() ? "Integration activated" : "Integration paused"
        ));
    }
}
