package com.geodevai.integration.buildium;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildiumProperty {
    private int id;
    private String name;
    private String rentalSubType;
    private int[] units;
    private BuildiumAddress address;

    @Getter
    @Setter
    public static class BuildiumAddress {
        private String addressLine1;
        private String addressLine2;
        private String addressLine3;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }
}
