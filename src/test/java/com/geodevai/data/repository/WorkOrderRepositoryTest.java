package com.geodevai.data.repository;

import com.geodevai.data.model.Person;
import com.geodevai.data.model.Property;
import com.geodevai.data.model.Unit;
import com.geodevai.data.model.WorkOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderRepositoryTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Test
    void testFindByProperty() {
        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        when(workOrderRepository.findByProperty(property)).thenReturn(List.of());
        List<WorkOrder> result = workOrderRepository.findByProperty(property);
        assertNotNull(result);
    }

    @Test
    void testFindByUnit() {
        Unit unit = new Unit();
        unit.setUnitId(UUID.randomUUID());
        when(workOrderRepository.findByUnit(unit)).thenReturn(List.of());
        List<WorkOrder> result = workOrderRepository.findByUnit(unit);
        assertNotNull(result);
    }

    @Test
    void testFindByStatus() {
        when(workOrderRepository.findByStatus("OPEN")).thenReturn(List.of());
        List<WorkOrder> result = workOrderRepository.findByStatus("OPEN");
        assertNotNull(result);
    }

    @Test
    void testFindByExternalWorkOrderId() {
        when(workOrderRepository.findByExternalWorkOrderId("EXT-001")).thenReturn(Optional.of(new WorkOrder()));
        Optional<WorkOrder> result = workOrderRepository.findByExternalWorkOrderId("EXT-001");
        assertTrue(result.isPresent());
    }

    @Test
    void testSaveWithPropertyUnitAndTenant() {
        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        Unit unit = new Unit();
        unit.setUnitId(UUID.randomUUID());
        Person tenant = new Person();
        tenant.setPersonId(UUID.randomUUID());

        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderId(UUID.randomUUID());
        workOrder.setProperty(property);
        workOrder.setUnit(unit);
        workOrder.setTenant(tenant);

        when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(workOrder);
        when(workOrderRepository.findById(workOrder.getWorkOrderId())).thenReturn(Optional.of(workOrder));

        WorkOrder saved = workOrderRepository.save(workOrder);
        Optional<WorkOrder> found = workOrderRepository.findById(saved.getWorkOrderId());

        assertNotNull(saved);
        assertTrue(found.isPresent());
        assertNotNull(found.get().getProperty());
        assertNotNull(found.get().getUnit());
    }
}
