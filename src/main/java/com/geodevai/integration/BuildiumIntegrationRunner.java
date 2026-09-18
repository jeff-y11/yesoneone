package com.geodevai.integration;

import com.geodevai.data.model.Integration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuildiumIntegrationRunner implements IntegrationRunner {

    private static final Logger logger = LoggerFactory.getLogger(BuildiumIntegrationRunner.class);

    @Override
    public String getType() {
        return IntegrationType.BUILDIUM.getTypeName();
    }

    @Override
    public List<Capability> getCapabilities() {
        return List.of(
                new Capability("Property", "PULL", false),
                new Capability("Unit", "PULL", false),
                new Capability("Tenant", "PULL", false),
                new Capability("WorkOrder", "PUSH+PULL", false)
        );
    }

    public String getApiKey(Integration integration) {
        return integration.getApiKey();
    }

    @Override
    public void run(Integration integration) {
        logger.info("Processing Buildium integration: {} (type: {})", integration.getName(), integration.getType());
    }
}
