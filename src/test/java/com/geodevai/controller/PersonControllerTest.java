package com.geodevai.controller;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Person;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.PersonRepository;
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
class PersonControllerTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PersonController controller;

    private MockMvc mockMvc;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testOrgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetPersons_ValidUser_ReturnsList() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        Person person = new Person();
        person.setPersonId(UUID.randomUUID());
        person.setFirstName("John");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(personRepository.findByOrganization(eq(org))).thenReturn(List.of(person));

        mockMvc.perform(get("/services/persons")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void testGetPersons_SearchQuery_ReturnsFiltered() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        Person person = new Person();
        person.setPersonId(UUID.randomUUID());
        person.setFirstName("John");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(personRepository.searchByOrganizationAndQuery(eq(org), eq("John"))).thenReturn(List.of(person));

        mockMvc.perform(get("/services/persons")
                .param("search", "John")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void testGetPerson_NotInOrg_ReturnsForbidden() throws Exception {
        User user = new User();
        user.setUserId(testUserId);
        Organization org = new Organization();
        org.setOrganizationId(testOrgId);
        user.setOrganization(org);

        Integration integration = new Integration();
        integration.setIntegrationId(UUID.randomUUID());
        Organization otherOrg = new Organization();
        otherOrg.setOrganizationId(UUID.randomUUID());
        integration.setOrganization(otherOrg);

        Person person = new Person();
        person.setPersonId(UUID.randomUUID());
        person.setIntegration(integration);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(user));
        when(personRepository.findById(any(UUID.class))).thenReturn(Optional.of(person));

        mockMvc.perform(get("/services/persons/{personId}", person.getPersonId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
