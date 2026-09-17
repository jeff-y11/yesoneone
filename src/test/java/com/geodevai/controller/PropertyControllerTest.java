package com.geodevai.controller;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.PropertyRepository;
import com.geodevai.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PropertyControllerTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PropertyController controller;

    private MockMvc mockMvc;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testOrgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUserId, null, Collections.emptyList()));
    }

    @Test
    void testGetProperties_ValidUser_ReturnsList() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setName("Test Property");
        property.setExternalPropertyId("EXT-001");
        property.setOrganization(org);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(propertyRepository.findByOrganization(eq(org))).thenReturn(List.of(property));

        mockMvc.perform(get("/services/properties")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void testGetProperties_SearchQuery_ReturnsFiltered() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setName("Test Property");
        property.setExternalPropertyId("EXT-001");
        property.setOrganization(org);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(propertyRepository.searchByOrganizationAndQuery(eq(org), eq("Test"))).thenReturn(List.of(property));

        mockMvc.perform(get("/services/properties")
                .param("search", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void testGetProperty_NotInOrg_ReturnsForbidden() throws Exception {
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

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(propertyRepository.findById(any(UUID.class))).thenReturn(Optional.of(property));

        mockMvc.perform(get("/services/properties/{propertyId}", property.getPropertyId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
