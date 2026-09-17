package com.geodevai.data.repository;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyRepositorySearchTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Test
    void testFindByOrganization_ReturnsPropertiesForOrg() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());

        Property property1 = new Property();
        property1.setPropertyId(UUID.randomUUID());
        property1.setOrganization(org);

        Property property2 = new Property();
        property2.setPropertyId(UUID.randomUUID());
        property2.setOrganization(org);

        when(propertyRepository.findByOrganization(eq(org))).thenReturn(List.of(property1, property2));

        List<Property> result = propertyRepository.findByOrganization(org);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getOrganization().equals(org)));
    }

    @Test
    void testSearchByOrganizationAndQuery_FiltersByName() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setName("Test Property");
        property.setOrganization(org);

        when(propertyRepository.searchByOrganizationAndQuery(eq(org), eq("Test"))).thenReturn(List.of(property));

        List<Property> result = propertyRepository.searchByOrganizationAndQuery(org, "Test");

        assertEquals(1, result.size());
        assertEquals("Test Property", result.get(0).getName());
    }

    @Test
    void testSearchByOrganizationAndQuery_EmptyQuery_ReturnsAllForOrg() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());

        Property property1 = new Property();
        property1.setPropertyId(UUID.randomUUID());
        property1.setOrganization(org);

        Property property2 = new Property();
        property2.setPropertyId(UUID.randomUUID());
        property2.setOrganization(org);

        when(propertyRepository.searchByOrganizationAndQuery(eq(org), eq(""))).thenReturn(List.of(property1, property2));

        List<Property> result = propertyRepository.searchByOrganizationAndQuery(org, "");

        assertEquals(2, result.size());
    }
}
