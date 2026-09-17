package com.geodevai.integration;

import com.geodevai.data.model.Integration;

public interface IntegrationRunner {
    String getType();
    void run(Integration integration);
}
