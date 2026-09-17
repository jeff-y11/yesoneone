package com.geodevai.data.repository;

import com.geodevai.data.model.Property;
import com.geodevai.data.model.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UnitRepositoryTest {

    @Mock
    private UnitRepository unitRepository;

    @Test
    void testFindByProperty() {
        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        when(unitRepository.findByProperty(property)).thenReturn(List.of());
        List<Unit> result = unitRepository.findByProperty(property);
        assertNotNull(result);
    }

    @Test
    void testFindByUnitNumberAndProperty() {
        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        when(unitRepository.findByUnitNumberAndProperty("1A", property)).thenReturn(Optional.of(new Unit()));
        Optional<Unit> result = unitRepository.findByUnitNumberAndProperty("1A", property);
        assertTrue(result.isPresent());
    }

    @Test
    void testSaveWithPropertyAndAddress() {
        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        Unit unit = new Unit();
        unit.setUnitId(UUID.randomUUID());
        unit.setProperty(property);

        when(unitRepository.save(any(Unit.class))).thenReturn(unit);
        when(unitRepository.findById(unit.getUnitId())).thenReturn(Optional.of(unit));

        Unit saved = unitRepository.save(unit);
        Optional<Unit> found = unitRepository.findById(saved.getUnitId());

        assertNotNull(saved);
        assertTrue(found.isPresent());
        assertNotNull(found.get().getProperty());
    }
}
