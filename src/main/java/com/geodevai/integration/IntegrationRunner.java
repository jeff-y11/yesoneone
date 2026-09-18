package com.geodevai.integration;

import com.geodevai.data.model.Integration;
import java.util.List;

public interface IntegrationRunner {
    String getType();
    List<Capability> getCapabilities();
    void run(Integration integration);
}
