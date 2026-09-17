package com.geodevai.integration;

public enum IntegrationType {
    BUILDIUM,
    APPOFOLIO;

    public String getTypeName() {
        return this.name();
    }

    public static IntegrationType fromString(String name) {
        try {
            return IntegrationType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
