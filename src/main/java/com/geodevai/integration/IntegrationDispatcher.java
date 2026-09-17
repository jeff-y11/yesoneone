package com.geodevai.integration;

import com.geodevai.data.model.Integration;
import com.geodevai.data.repository.IntegrationRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class IntegrationDispatcher {

    private final Map<String, IntegrationRunner> runners = new HashMap<>();

    public IntegrationDispatcher(List<IntegrationRunner> allRunners) {
        for (IntegrationRunner runner : allRunners) {
            runners.put(runner.getType(), runner);
        }
    }

    public void dispatch(UUID integrationId, IntegrationRepository integrationRepository) {
        Integration integration = integrationRepository.findById(integrationId)
                .orElseThrow(() -> new RuntimeException("Integration not found: " + integrationId));
        IntegrationRunner runner = runners.get(integration.getType());
        if (runner == null) {
            throw new RuntimeException("No runner registered for type: " + integration.getType());
        }
        runner.run(integration);
    }
}
