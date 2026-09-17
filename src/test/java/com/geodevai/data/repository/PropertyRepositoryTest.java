package com.geodevai.data.repository;

import com.geodevai.data.model.Address;
import com.geodevai.data.model.Integration;
import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PropertyRepositoryTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Test
    void testFindByOrganization() {
        Organization org = new Organization();
        org.setOrganizationId(UUID.randomUUID());
        when(propertyRepository.findByOrganization(org)).thenReturn(List.of());
        List<Property> result = propertyRepository.findByOrganization(org);
        assertNotNull(result);
    }

    @Test
    void testFindByExternalPropertyId() {
        when(propertyRepository.findByExternalPropertyId("EXT-123")).thenReturn(Optional.of(new Property()));
        Optional<Property> result = propertyRepository.findByExternalPropertyId("EXT-123");
        assertTrue(result.isPresent());
    }

    @Test
    void testSaveWithAddress() {
        Address address = new Address();
        address.setAddressId(UUID.randomUUID());
        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setAddress(address);

        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(propertyRepository.findById(property.getPropertyId())).thenReturn(Optional.of(property));

        Property saved = propertyRepository.save(property);
        Optional<Property> found = propertyRepository.findById(saved.getPropertyId());

        assertNotNull(saved);
        assertTrue(found.isPresent());
        assertNotNull(found.get().getAddress());
    }
}
