package com.geodevai.controller;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import com.geodevai.data.model.User;
import com.geodevai.data.model.WorkOrder;
import com.geodevai.data.repository.PropertyRepository;
import com.geodevai.data.repository.UserRepository;
import com.geodevai.data.repository.WorkOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderControllerTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkOrderController controller;

    private MockMvc mockMvc;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testOrgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetWorkOrders_ValidUser_ReturnsList() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        WorkOrder wo = new WorkOrder();
        wo.setWorkOrderId(UUID.randomUUID());
        wo.setTitle("Fix leaky faucet");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(workOrderRepository.findByOrganization(eq(org))).thenReturn(List.of(wo));

        mockMvc.perform(get("/services/work-orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void testGetWorkOrders_WithStatus_ReturnsFiltered() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setOrganization(org);

        WorkOrder wo = new WorkOrder();
        wo.setWorkOrderId(UUID.randomUUID());
        wo.setTitle("Fix leaky faucet");
        wo.setStatus("OPEN");
        wo.setProperty(property);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(workOrderRepository.findByStatus(eq("OPEN"))).thenReturn(List.of(wo));

        mockMvc.perform(get("/services/work-orders")
                .param("status", "OPEN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void testGetWorkOrder_NotInOrg_ReturnsForbidden() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        Organization otherOrg = new Organization();
        otherOrg.setOrganizationId(UUID.randomUUID());
        property.setOrganization(otherOrg);

        WorkOrder wo = new WorkOrder();
        wo.setWorkOrderId(UUID.randomUUID());
        wo.setProperty(property);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(workOrderRepository.findById(any(UUID.class))).thenReturn(Optional.of(wo));

        mockMvc.perform(get("/services/work-orders/{workOrderId}", wo.getWorkOrderId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
