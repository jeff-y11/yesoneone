package com.geodevai.data.repository;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import com.geodevai.data.model.WorkOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderRepositorySearchTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Test
    void testFindByOrganization_ReturnsWorkOrdersThroughPropertyJoin() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setOrganization(org);

        WorkOrder wo1 = new WorkOrder();
        wo1.setWorkOrderId(UUID.randomUUID());
        wo1.setProperty(property);

        WorkOrder wo2 = new WorkOrder();
        wo2.setWorkOrderId(UUID.randomUUID());
        wo2.setProperty(property);

        when(workOrderRepository.findByOrganization(eq(org))).thenReturn(List.of(wo1, wo2));

        List<WorkOrder> result = workOrderRepository.findByOrganization(org);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(wo -> wo.getProperty().getOrganization().equals(org)));
    }

    @Test
    void testSearchByOrganizationAndQuery_SearchByTitle() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setOrganization(org);

        WorkOrder wo = new WorkOrder();
        wo.setWorkOrderId(UUID.randomUUID());
        wo.setTitle("Fix leaky faucet");
        wo.setProperty(property);

        when(workOrderRepository.searchByOrganizationAndQuery(eq(org), eq("faucet"))).thenReturn(List.of(wo));

        List<WorkOrder> result = workOrderRepository.searchByOrganizationAndQuery(org, "faucet");

        assertEquals(1, result.size());
        assertEquals("Fix leaky faucet", result.get(0).getTitle());
    }
}
