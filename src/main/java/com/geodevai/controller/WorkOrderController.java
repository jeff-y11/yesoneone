package com.geodevai.controller;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.User;
import com.geodevai.data.model.WorkOrder;
import com.geodevai.data.repository.PropertyRepository;
import com.geodevai.data.repository.WorkOrderRepository;
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
@RequestMapping("/services/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderRepository workOrderRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWorkOrders(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID propertyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        if (user.getOrganization() == null) {
            return ResponseEntity.status(403).build();
        }

        Organization org = user.getOrganization();
        List<WorkOrder> workOrders;

        if (propertyId != null) {
            return propertyRepository.findById(propertyId)
                    .filter(property -> property.getOrganization().getOrganizationId()
                            .equals(org.getOrganizationId()))
                    .map(property -> {
                        List<WorkOrder> propertyWorkOrders = workOrderRepository.findByProperty(property);
                        if (status != null && !status.isBlank()) {
                            propertyWorkOrders = propertyWorkOrders.stream()
                                    .filter(wo -> wo.getStatus().equalsIgnoreCase(status))
                                    .toList();
                        }
                        return ResponseEntity.ok((Map<String, Object>) Map.of(
                                "items", propertyWorkOrders,
                                "count", propertyWorkOrders.size(),
                                "query", search
                        ));
                    })
                    .orElse(ResponseEntity.status(403).build());
        }

        if (status != null && !status.isBlank()) {
            workOrders = workOrderRepository.findByStatus(status).stream()
                    .filter(wo -> wo.getProperty().getOrganization().getOrganizationId()
                            .equals(org.getOrganizationId()))
                    .toList();
        } else if (search.isBlank()) {
            workOrders = workOrderRepository.findByOrganization(org);
        } else {
            workOrders = workOrderRepository.searchByOrganizationAndQuery(org, search);
        }

        return ResponseEntity.ok(Map.of(
                "items", workOrders,
                "count", workOrders.size(),
                "query", search
        ));
    }

    @GetMapping("/{workOrderId}")
    public ResponseEntity<Map<String, Object>> getWorkOrder(@PathVariable UUID workOrderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        if (user.getOrganization() == null) {
            return ResponseEntity.status(403).build();
        }

        return workOrderRepository.findById(workOrderId)
                .filter(wo -> wo.getProperty().getOrganization().getOrganizationId()
                        .equals(user.getOrganization().getOrganizationId()))
                .map(wo -> ResponseEntity.ok((Map<String, Object>) Map.of(
                        "workOrderId", wo.getWorkOrderId(),
                        "externalWorkOrderId", wo.getExternalWorkOrderId(),
                        "title", wo.getTitle(),
                        "summary", wo.getSummary() != null ? wo.getSummary() : "",
                        "status", wo.getStatus(),
                        "priority", wo.getPriority() != null ? wo.getPriority() : "",
                        "amount", wo.getAmount() != null ? wo.getAmount() : 0,
                        "dueDate", wo.getDueDate() != null ? wo.getDueDate() : "",
                        "completionDate", wo.getCompletionDate() != null ? wo.getCompletionDate() : "",
                        "propertyId", wo.getProperty() != null ? wo.getProperty().getPropertyId() : null,
                        "propertyName", wo.getProperty() != null ? wo.getProperty().getName() : "",
                        "unitId", wo.getUnit() != null ? wo.getUnit().getUnitId() : null,
                        "unitNumber", wo.getUnit() != null ? wo.getUnit().getUnitNumber() : "",
                        "tenantId", wo.getTenant() != null ? wo.getTenant().getPersonId() : null,
                        "tenantName", wo.getTenant() != null ? wo.getTenant().getFirstName() + " " + wo.getTenant().getLastName() : "",
                        "callSource", wo.getCallSource() != null ? wo.getCallSource() : "",
                        "callerName", wo.getCallerName() != null ? wo.getCallerName() : "",
                        "callerContactInfo", wo.getCallerContactInfo() != null ? wo.getCallerContactInfo() : "",
                        "lastSyncTime", wo.getLastSyncTime() != null ? wo.getLastSyncTime() : ""
                )))
                .orElse(ResponseEntity.status(403).build());
    }
}
