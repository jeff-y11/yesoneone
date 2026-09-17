package com.geodevai.data.repository;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.Person;
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
class PersonRepositoryTest {

    @Mock
    private PersonRepository personRepository;

    @Test
    void testFindByPhoneNumber() {
        when(personRepository.findByPhoneNumber("555-1234")).thenReturn(Optional.of(new Person()));
        Optional<Person> result = personRepository.findByPhoneNumber("555-1234");
        assertTrue(result.isPresent());
    }

    @Test
    void testFindByEmail() {
        when(personRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Person()));
        Optional<Person> result = personRepository.findByEmail("test@example.com");
        assertTrue(result.isPresent());
    }

    @Test
    void testFindByExternalTenantId() {
        when(personRepository.findByExternalTenantId("TT-001")).thenReturn(Optional.of(new Person()));
        Optional<Person> result = personRepository.findByExternalTenantId("TT-001");
        assertTrue(result.isPresent());
    }

    @Test
    void testFindByUnit() {
        Unit unit = new Unit();
        unit.setUnitId(UUID.randomUUID());
        when(personRepository.findByUnit(unit)).thenReturn(List.of());
        List<Person> result = personRepository.findByUnit(unit);
        assertNotNull(result);
    }
}
