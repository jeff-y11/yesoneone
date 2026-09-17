package com.geodevai.controller;

import com.geodevai.data.dto.SearchResponse;
import com.geodevai.data.dto.WorkOrderResponse;
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
import java.util.UUID;

@RestController
@RequestMapping("/services/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderRepository workOrderRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<SearchResponse<WorkOrderResponse>> getWorkOrders(
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
                        List<WorkOrderResponse> items = propertyWorkOrders.stream()
                                .map(this::toResponse)
                                .toList();
                        return ResponseEntity.ok(new SearchResponse<>(items, items.size(), search));
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

        List<WorkOrderResponse> items = workOrders.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new SearchResponse<>(items, items.size(), search));
    }

    @GetMapping("/{workOrderId}")
    public ResponseEntity<WorkOrderResponse> getWorkOrder(@PathVariable UUID workOrderId) {
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
                .map(wo -> ResponseEntity.ok(toResponse(wo)))
                .orElse(ResponseEntity.status(403).build());
    }

    private WorkOrderResponse toResponse(WorkOrder wo) {
        String tenantName = null;
        if (wo.getTenant() != null) {
            String first = wo.getTenant().getFirstName() != null ? wo.getTenant().getFirstName() : "";
            String last = wo.getTenant().getLastName() != null ? wo.getTenant().getLastName() : "";
            tenantName = (first + " " + last).trim();
        }

        return new WorkOrderResponse(
                wo.getWorkOrderId(),
                wo.getExternalWorkOrderId(),
                wo.getTitle(),
                wo.getSummary(),
                wo.getStatus(),
                wo.getPriority(),
                wo.getAmount(),
                wo.getDueDate(),
                wo.getCompletionDate(),
                wo.getProperty() != null ? wo.getProperty().getPropertyId() : null,
                wo.getProperty() != null ? wo.getProperty().getName() : null,
                wo.getUnit() != null ? wo.getUnit().getUnitId() : null,
                wo.getUnit() != null ? wo.getUnit().getUnitNumber() : null,
                wo.getTenant() != null ? wo.getTenant().getPersonId() : null,
                tenantName,
                wo.getCallSource(),
                wo.getCallerName(),
                wo.getCallerContactInfo(),
                wo.getLastSyncTime()
        );
    }
}
