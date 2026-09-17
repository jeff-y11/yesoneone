package com.geodevai.integration;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.IntegrationSchedule;
import com.geodevai.data.repository.IntegrationRepository;
import com.geodevai.data.repository.IntegrationScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IntegrationJobServiceTest {

    @Mock
    private IntegrationScheduleRepository scheduleRepository;

    @Mock
    private IntegrationDispatcher dispatcher;

    @Mock
    private IntegrationRepository integrationRepository;

    @InjectMocks
    private IntegrationJobService jobService;

    @Test
    void testRunScheduledIntegrations_ActiveSchedule_CallsDispatch() {
        Integration integration = new Integration();
        integration.setIntegrationId(UUID.randomUUID());
        integration.setType("BUILDIUM");
        integration.setActive(true);

        IntegrationSchedule schedule = new IntegrationSchedule();
        schedule.setScheduleId(UUID.randomUUID());
        schedule.setIntegration(integration);
        schedule.setCronExpression("0 */5 * * * *");
        schedule.setNextRunTime(LocalDateTime.now().minusMinutes(10));
        schedule.setCurrentRetryCount(0);
        schedule.setMaxRetries(3);

        when(scheduleRepository.findByActiveIsTrue()).thenReturn(List.of(schedule));

        jobService.runScheduledIntegrations();

        verify(dispatcher, times(1)).dispatch(eq(integration.getIntegrationId()), eq(integrationRepository));
        verify(scheduleRepository, times(1)).save(schedule);
    }

    @Test
    void testRunScheduledIntegrations_InactiveSchedule_Skips() {
        IntegrationSchedule schedule = new IntegrationSchedule();
        schedule.setScheduleId(UUID.randomUUID());
        schedule.setCronExpression("0 */5 * * * *");
        schedule.setNextRunTime(LocalDateTime.now().plusMinutes(10));
        schedule.setCurrentRetryCount(0);
        schedule.setMaxRetries(3);

        when(scheduleRepository.findByActiveIsTrue()).thenReturn(List.of(schedule));

        jobService.runScheduledIntegrations();

        verify(dispatcher, never()).dispatch(any(UUID.class), any());
    }

    @Test
    void testRunScheduledIntegrations_MaxRetries_DisablesSchedule() {
        Integration integration = new Integration();
        integration.setIntegrationId(UUID.randomUUID());
        integration.setType("BUILDIUM");
        integration.setActive(true);

        IntegrationSchedule schedule = new IntegrationSchedule();
        schedule.setScheduleId(UUID.randomUUID());
        schedule.setIntegration(integration);
        schedule.setCronExpression("0 */5 * * * *");
        schedule.setNextRunTime(LocalDateTime.now().minusMinutes(10));
        schedule.setCurrentRetryCount(2);
        schedule.setMaxRetries(3);
        schedule.setActive(true);

        when(scheduleRepository.findByActiveIsTrue()).thenReturn(List.of(schedule));
        doThrow(new RuntimeException("Dispatch failed")).when(dispatcher).dispatch(any(UUID.class), any());

        jobService.runScheduledIntegrations();

        verify(scheduleRepository, times(1)).save(schedule);
        assertEquals(3, schedule.getCurrentRetryCount());
        assertFalse(schedule.getActive());
    }
}
