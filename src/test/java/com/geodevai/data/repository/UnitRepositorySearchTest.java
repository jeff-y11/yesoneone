package com.geodevai.data.repository;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import com.geodevai.data.model.Unit;
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
class UnitRepositorySearchTest {

    @Mock
    private UnitRepository unitRepository;

    @Test
    void testFindByOrganization_ReturnsUnitsThroughPropertyJoin() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setOrganization(org);

        Unit unit1 = new Unit();
        unit1.setUnitId(UUID.randomUUID());
        unit1.setProperty(property);

        Unit unit2 = new Unit();
        unit2.setUnitId(UUID.randomUUID());
        unit2.setProperty(property);

        when(unitRepository.findByOrganization(eq(org))).thenReturn(List.of(unit1, unit2));

        List<Unit> result = unitRepository.findByOrganization(org);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> u.getProperty().getOrganization().equals(org)));
    }

    @Test
    void testSearchByOrganizationAndQuery_SearchByUnitNumber() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());

        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setOrganization(org);

        Unit unit = new Unit();
        unit.setUnitId(UUID.randomUUID());
        unit.setUnitNumber("101");
        unit.setProperty(property);

        when(unitRepository.searchByOrganizationAndQuery(eq(org), eq("101"))).thenReturn(List.of(unit));

        List<Unit> result = unitRepository.searchByOrganizationAndQuery(org, "101");

        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getUnitNumber());
    }
}
