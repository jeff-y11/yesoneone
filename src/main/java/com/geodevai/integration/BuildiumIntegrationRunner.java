package com.geodevai.integration;

import com.geodevai.data.model.*;
import com.geodevai.data.repository.*;
import com.geodevai.integration.buildium.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class BuildiumIntegrationRunner implements IntegrationRunner {

    private static final Logger logger = LoggerFactory.getLogger(BuildiumIntegrationRunner.class);

    private final BuildiumApiClient apiClient;
    private final IntegrationRepository integrationRepository;
    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final PersonRepository personRepository;
    private final WorkOrderRepository workOrderRepository;

    @Override
    public String getType() {
        return IntegrationType.BUILDIUM.getTypeName();
    }

    @Override
    public List<Capability> getCapabilities() {
        return List.of(
                new Capability("Property", "PULL", false),
                new Capability("Unit", "PULL", false),
                new Capability("Tenant", "PULL", false),
                new Capability("WorkOrder", "PUSH+PULL", false)
        );
    }

    @Override
    public void run(Integration integration) {
        String apiKey = getApiKey(integration);
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("No API key configured for integration: {}", integration.getName());
            return;
        }

        Organization org = integration.getOrganization();
        if (org == null) {
            logger.warn("No organization associated with integration: {}", integration.getName());
            return;
        }

        Set<String> entityTypes = getSelectedEntityTypes(integration);
        logger.info("Starting Buildium sync for integration: {} with entities: {}", integration.getName(), entityTypes);

        if (entityTypes.contains("Property")) {
            syncProperties(apiKey, org);
        }
        if (entityTypes.contains("Unit")) {
            syncUnits(apiKey, org);
        }
        if (entityTypes.contains("Tenant")) {
            syncTenants(apiKey, org);
        }
        if (entityTypes.contains("WorkOrder")) {
            syncWorkOrders(apiKey, org);
        }

        logger.info("Buildium sync completed for integration: {}", integration.getName());
    }

    private Set<String> getSelectedEntityTypes(Integration integration) {
        Set<String> types = new HashSet<>();
        if (integration.getCapabilities() != null) {
            for (IntegrationCapabilities cap : integration.getCapabilities()) {
                types.add(cap.getEntityType());
            }
        }
        if (types.isEmpty()) {
            types.addAll(List.of("Property", "Unit", "Tenant", "WorkOrder"));
        }
        return types;
    }

    private void syncProperties(String apiKey, Organization org) {
        List<BuildiumProperty> properties = apiClient.getList("rentals", apiKey, BuildiumProperty.class);
        for (BuildiumProperty bp : properties) {
            Property property = propertyRepository.findByExternalPropertyId(String.valueOf(bp.getId()))
                    .orElseGet(() -> new Property());

            property.setName(bp.getName());
            property.setExternalPropertyId(String.valueOf(bp.getId()));
            property.setPropertyType(bp.getRentalSubType());
            property.setOrganization(org);

            if (bp.getAddress() != null) {
                Address address = property.getAddress();
                if (address == null) {
                    address = new Address();
                    property.setAddress(address);
                }
                address.setAddressLine1(bp.getAddress().getAddressLine1());
                address.setAddressLine2(bp.getAddress().getAddressLine2());
                address.setCity(bp.getAddress().getCity());
                address.setState(bp.getAddress().getState());
                address.setPostalCode(bp.getAddress().getPostalCode());
                address.setCountry(bp.getAddress().getCountry());
            }

            propertyRepository.save(property);
        }
        logger.info("Synced {} properties", properties.size());
    }

    private void syncUnits(String apiKey, Organization org) {
        List<BuildiumUnit> units = apiClient.getList("rentals/units", apiKey, BuildiumUnit.class);
        for (BuildiumUnit bu : units) {
            Unit unit = unitRepository.findByExternalUnitId(String.valueOf(bu.getId()))
                    .orElseGet(() -> new Unit());

            unit.setUnitNumber(bu.getUnitNumber());
            unit.setExternalUnitId(String.valueOf(bu.getId()));
            unit.setSquareFootage(bu.getUnitSize());
            unit.setBedrooms(bu.getUnitBedrooms());
            unit.setBathrooms(bu.getUnitBathrooms());
            unit.setRentAmount(bu.getMarketRent() != 0 ? unit.getRentAmount() : null);
            unit.setIsOccupied(bu.isUnitOccupied());

            if (bu.getPropertyId() > 0) {
                Property property = propertyRepository.findByExternalPropertyId(String.valueOf(bu.getPropertyId()))
                        .orElse(null);
                if (property != null) {
                    unit.setProperty(property);
                }
            }

            unitRepository.save(unit);
        }
        logger.info("Synced {} units", units.size());
    }

    private void syncTenants(String apiKey, Organization org) {
        List<BuildiumTenant> tenants = apiClient.getList("associations/tenants", apiKey, BuildiumTenant.class);
        for (BuildiumTenant bt : tenants) {
            Person person = personRepository.findByExternalTenantId(String.valueOf(bt.getId()))
                    .orElseGet(() -> new Person());

            person.setFirstName(bt.getFirstName());
            person.setLastName(bt.getLastName());
            person.setExternalTenantId(String.valueOf(bt.getId()));
            person.setEmail(bt.getEmail());
            if (bt.getPhoneNumbers() != null && bt.getPhoneNumbers().length > 0) {
                person.setPhoneNumber(bt.getPhoneNumbers()[0]);
            }
            if (bt.getLeases() != null && !bt.getLeases().isEmpty()) {
                person.setLeaseStartDate(java.time.LocalDateTime.parse(bt.getLeases().get(0).getStartDate()));
                person.setLeaseEndDate(java.time.LocalDateTime.parse(bt.getLeases().get(0).getEndDate()));
            }

            Integration integration = integrationRepository.findByOrganizationAndActiveIsTrue(org).stream().findFirst().orElse(null);
            if (integration != null) {
                person.setIntegration(integration);
            }

            personRepository.save(person);
        }
        logger.info("Synced {} tenants", tenants.size());
    }

    private void syncWorkOrders(String apiKey, Organization org) {
        List<BuildiumWorkOrder> workOrders = apiClient.getList("workorders", apiKey, BuildiumWorkOrder.class);
        for (BuildiumWorkOrder wo : workOrders) {
            WorkOrder order = workOrderRepository.findByExternalWorkOrderId(String.valueOf(wo.getId()))
                    .orElseGet(() -> new WorkOrder());

            order.setTitle(wo.getTitle());
            order.setExternalWorkOrderId(String.valueOf(wo.getId()));
            order.setSummary(wo.getTitle());
            order.setWorkDetails(wo.getWorkDetails());
            order.setStatus(wo.getStatus());
            order.setPriority(wo.getPriority());
            order.setAmount(wo.getAmount() != 0 ? order.getAmount() : null);
            order.setDueDate(wo.getDueDate() != null ? java.time.LocalDateTime.parse(wo.getDueDate()) : null);
            order.setEntryNotes(wo.getEntryNotes());
            order.setVendorNotes(wo.getVendorNotes());
            order.setInvoiceNumber(wo.getInvoiceNumber());
            order.setChargeableTo(wo.getChargeableTo());

            if (wo.getPropertyId() > 0) {
                Property property = propertyRepository.findByExternalPropertyId(String.valueOf(wo.getPropertyId()))
                        .orElse(null);
                if (property != null) {
                    order.setProperty(property);
                }
            }
            if (wo.getUnitId() != null && wo.getUnitId() > 0) {
                Unit unit = unitRepository.findByExternalUnitId(String.valueOf(wo.getUnitId()))
                        .orElse(null);
                if (unit != null) {
                    order.setUnit(unit);
                }
            }
            if (wo.getTenantId() != null && wo.getTenantId() > 0) {
                Person tenant = personRepository.findByExternalTenantId(String.valueOf(wo.getTenantId()))
                        .orElse(null);
                if (tenant != null) {
                    order.setTenant(tenant);
                }
            }

            workOrderRepository.save(order);
        }
        logger.info("Synced {} work orders", workOrders.size());
    }

    private Integration findIntegrationForPerson(Person person, Organization org) {
        return person.getIntegration();
    }

    public String getApiKey(Integration integration) {
        return integration.getApiKey();
    }
}
