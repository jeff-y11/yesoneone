package com.geodevai.data.repository;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Person;
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
class PersonRepositorySearchTest {

    @Mock
    private PersonRepository personRepository;

    @Test
    void testFindByOrganization_ReturnsPersonsThroughIntegrationJoin() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());

        Integration integration = new Integration();
        integration.setIntegrationId(UUID.randomUUID());
        integration.setOrganization(org);

        Person person1 = new Person();
        person1.setPersonId(UUID.randomUUID());
        person1.setIntegration(integration);

        Person person2 = new Person();
        person2.setPersonId(UUID.randomUUID());
        person2.setIntegration(integration);

        when(personRepository.findByOrganization(eq(org))).thenReturn(List.of(person1, person2));

        List<Person> result = personRepository.findByOrganization(org);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getIntegration().getOrganization().equals(org)));
    }

    @Test
    void testSearchByOrganizationAndQuery_SearchByName() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());

        Integration integration = new Integration();
        integration.setIntegrationId(UUID.randomUUID());
        integration.setOrganization(org);

        Person person = new Person();
        person.setPersonId(UUID.randomUUID());
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setIntegration(integration);

        when(personRepository.searchByOrganizationAndQuery(eq(org), eq("John"))).thenReturn(List.of(person));

        List<Person> result = personRepository.searchByOrganizationAndQuery(org, "John");

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }
}
