package com.geodevai.integration;

import com.geodevai.data.model.Integration;
import com.geodevai.data.repository.IntegrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class IntegrationDispatcherTest {

    private IntegrationRepository integrationRepository;
    private IntegrationRunner buildiumRunner;
    private IntegrationDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        integrationRepository = mock(IntegrationRepository.class);
        buildiumRunner = mock(IntegrationRunner.class);
        when(buildiumRunner.getType()).thenReturn("BUILDIUM");
        dispatcher = new IntegrationDispatcher(List.of(buildiumRunner));
    }

    @Test
    void testDispatch_ValidType_CallsRunner() {
        Integration integration = new Integration();
        integration.setIntegrationId(UUID.randomUUID());
        integration.setType("BUILDIUM");

        when(integrationRepository.findById(any(UUID.class))).thenReturn(Optional.of(integration));

        dispatcher.dispatch(integration.getIntegrationId(), integrationRepository);

        verify(buildiumRunner, times(1)).run(integration);
    }

    @Test
    void testDispatch_InvalidType_ThrowsRuntimeException() {
        Integration integration = new Integration();
        integration.setIntegrationId(UUID.randomUUID());
        integration.setType("UNKNOWN");

        when(integrationRepository.findById(any(UUID.class))).thenReturn(Optional.of(integration));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
            dispatcher.dispatch(integration.getIntegrationId(), integrationRepository)
        );
        assertTrue(thrown.getMessage().contains("No runner registered for type"));
    }

    @Test
    void testConstructor_RegisterAllRunners() {
        assertNotNull(dispatcher);
        verify(buildiumRunner, never()).run(any());
    }
}
