package com.geodevai.integration.buildium;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildiumUnit {
    private int id;
    private String unitNumber;
    private int unitSize;
    private int unitBedrooms;
    private int unitBathrooms;
    private double marketRent;
    private boolean isUnitOccupied;
    private String buildingName;
    private boolean isUnitListed;
    private int propertyId;
}
