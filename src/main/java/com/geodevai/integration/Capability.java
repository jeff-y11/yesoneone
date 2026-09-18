package com.geodevai.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Capability {
    private final String entityType;
    private final String direction;
    private final boolean required;
}
