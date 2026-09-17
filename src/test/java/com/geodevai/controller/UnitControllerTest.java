package com.geodevai.controller;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import com.geodevai.data.model.Unit;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.PropertyRepository;
import com.geodevai.data.repository.UnitRepository;
import com.geodevai.data.repository.UserRepository;
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
class UnitControllerTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UnitController controller;

    private MockMvc mockMvc;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testOrgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetUnits_ValidUser_ReturnsList() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        Unit unit = new Unit();
        unit.setUnitId(UUID.randomUUID());
        unit.setUnitNumber("101");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(unitRepository.findByOrganization(eq(org))).thenReturn(List.of(unit));

        mockMvc.perform(get("/services/units")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void testGetUnits_WithPropertyId_ReturnsFiltered() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setOrganization(org);

        Unit unit = new Unit();
        unit.setUnitId(UUID.randomUUID());
        unit.setUnitNumber("101");
        unit.setProperty(property);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(propertyRepository.findById(eq(property.getPropertyId()))).thenReturn(Optional.of(property));
        when(unitRepository.findByProperty(eq(property))).thenReturn(List.of(unit));

        mockMvc.perform(get("/services/units")
                .param("propertyId", property.getPropertyId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void testGetUnit_NotInOrg_ReturnsForbidden() throws Exception {
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

        Unit unit = new Unit();
        unit.setUnitId(UUID.randomUUID());
        unit.setProperty(property);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(unitRepository.findById(any(UUID.class))).thenReturn(Optional.of(unit));

        mockMvc.perform(get("/services/units/{unitId}", unit.getUnitId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
