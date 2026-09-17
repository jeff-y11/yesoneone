package com.geodevai.controller;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.Organization;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.IntegrationRepository;
import com.geodevai.data.repository.OrganizationRepository;
import com.geodevai.data.repository.UserRepository;
import com.geodevai.integration.IntegrationDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IntegrationControllerTest {

    @Mock
    private IntegrationRepository integrationRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IntegrationDispatcher integrationDispatcher;

    @InjectMocks
    private IntegrationController controller;

    private MockMvc mockMvc;

    private final UUID testIntegrationId = UUID.randomUUID();
    private final UUID testUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testRunIntegration_ValidUserAndIntegration_ReturnsRunning() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());
        user.setOrganization(org);

        Integration integration = new Integration();
        integration.setIntegrationId(testIntegrationId);
        integration.setActive(true);
        integration.setOrganization(org);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(integrationRepository.findById(testIntegrationId)).thenReturn(Optional.of(integration));

        mockMvc.perform(post("/services/integrations/{integrationId}/run", testIntegrationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.integrationId").value(testIntegrationId.toString()));
    }

    @Test
    void testRunIntegration_UserNotInOrg_ReturnsForbidden() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());
        user.setOrganization(org);

        Integration integration = new Integration();
        integration.setIntegrationId(testIntegrationId);
        Organization otherOrg = new Organization();
        otherOrg.setOrganizationId(UUID.randomUUID());
        integration.setOrganization(otherOrg);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(integrationRepository.findById(testIntegrationId)).thenReturn(Optional.of(integration));

        mockMvc.perform(post("/services/integrations/{integrationId}/run", testIntegrationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRunIntegration_InactiveIntegration_ReturnsBadRequest() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());
        user.setOrganization(org);

        Integration integration = new Integration();
        integration.setIntegrationId(testIntegrationId);
        integration.setActive(false);
        integration.setOrganization(org);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(integrationRepository.findById(testIntegrationId)).thenReturn(Optional.of(integration));

        mockMvc.perform(post("/services/integrations/{integrationId}/run", testIntegrationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Integration is not active"));
    }

    @Test
    void testCreateIntegration_WithType() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());
        user.setOrganization(org);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/services/integrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Test Integration","endpoint":"http://test.com","type":"BUILDIUM","parameters":{}}
                    """))
                .andExpect(status().isOk());

        verify(integrationRepository, times(1)).save(any(Integration.class));
    }

    @Test
    void testToggleActive_ValidUser_TogglesActive() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());
        user.setOrganization(org);

        Integration integration = new Integration();
        integration.setIntegrationId(testIntegrationId);
        integration.setActive(true);
        integration.setOrganization(org);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(integrationRepository.findById(testIntegrationId)).thenReturn(Optional.of(integration));

        mockMvc.perform(post("/services/integrations/{integrationId}/toggle-active", testIntegrationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.message").value("Integration paused"));

        verify(integrationRepository, times(1)).save(integration);
    }

    @Test
    void testToggleActive_UserNotInOrg_ReturnsForbidden() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());
        user.setOrganization(org);

        Integration integration = new Integration();
        integration.setIntegrationId(testIntegrationId);
        Organization otherOrg = new Organization();
        otherOrg.setOrganizationId(UUID.randomUUID());
        integration.setOrganization(otherOrg);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(integrationRepository.findById(testIntegrationId)).thenReturn(Optional.of(integration));

        mockMvc.perform(post("/services/integrations/{integrationId}/toggle-active", testIntegrationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testToggleActive_TogglesBackAndForth() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());
        user.setOrganization(org);

        Integration integration = new Integration();
        integration.setIntegrationId(testIntegrationId);
        integration.setActive(false);
        integration.setOrganization(org);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(integrationRepository.findById(testIntegrationId)).thenReturn(Optional.of(integration));

        mockMvc.perform(post("/services/integrations/{integrationId}/toggle-active", testIntegrationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.message").value("Integration activated"));
    }
}
