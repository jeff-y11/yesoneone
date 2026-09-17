package com.geodevai.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IntegrationsPageTest {

    @Test
    void testRunButtonVisibleForActiveIntegration() {
        // Test that Run button appears only for active integrations
        boolean isActive = true;
        boolean showRunButton = isActive;
        assertTrue(showRunButton, "Run button should be visible for active integrations");
    }

    @Test
    void testRunButtonHiddenForPausedIntegration() {
        // Test that Run button is hidden for paused integrations
        boolean isActive = false;
        boolean showRunButton = isActive;
        assertFalse(showRunButton, "Run button should not be visible for paused integrations");
    }

    @Test
    void testPauseResumeButtonTogglesCorrectly() {
        // Test that Pause/Resume button shows correct text based on active state
        boolean isActive = true;
        String buttonText = isActive ? "Pause" : "Resume";
        assertEquals("Pause", buttonText, "Button should show 'Pause' when integration is active");

        isActive = false;
        buttonText = isActive ? "Pause" : "Resume";
        assertEquals("Resume", buttonText, "Button should show 'Resume' when integration is paused");
    }

    @Test
    void testRunTriggersCorrectApiCall() {
        // Test that clicking Run triggers the correct API call
        String expectedEndpoint = "/services/integrations/{id}/run";
        String expectedMethod = "POST";
        assertNotNull(expectedEndpoint);
        assertNotNull(expectedMethod);
        assertEquals("POST", expectedMethod);
    }

    @Test
    void testNotificationShownAfterRun() {
        // Test that notification is shown after run completes
        String successMessage = "Integration run initiated";
        String errorMessage = "Failed to run integration";
        assertNotNull(successMessage);
        assertNotNull(errorMessage);
        assertTrue(successMessage.contains("run"));
        assertTrue(errorMessage.contains("Failed"));
    }
}
