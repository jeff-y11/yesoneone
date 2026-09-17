package com.geodevai.data.repository;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.IntegrationSchedule;
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
class IntegrationScheduleRepositoryTest {

    @Mock
    private IntegrationScheduleRepository scheduleRepository;

    @Test
    void testFindByActiveIsTrue() {
        when(scheduleRepository.findByActiveIsTrue()).thenReturn(List.of());
        List<IntegrationSchedule> result = scheduleRepository.findByActiveIsTrue();
        assertNotNull(result);
    }

    @Test
    void testFindByIntegration() {
        IntegrationSchedule schedule = new IntegrationSchedule();
        schedule.setScheduleId(UUID.randomUUID());
        when(scheduleRepository.findByIntegration(any(Integration.class))).thenReturn(Optional.of(schedule));
        Optional<IntegrationSchedule> found = scheduleRepository.findByIntegration(new Integration());
        assertTrue(found.isPresent());
        assertEquals(schedule.getScheduleId(), found.get().getScheduleId());
    }
}
