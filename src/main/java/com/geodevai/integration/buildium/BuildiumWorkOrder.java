package com.geodevai.integration.buildium;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildiumWorkOrder {
    private int id;
    private String title;
    private String workDetails;
    private String status;
    private String priority;
    private double amount;
    private String dueDate;
    private String entryNotes;
    private String vendorNotes;
    private String invoiceNumber;
    private String chargeableTo;
    private int propertyId;
    private Integer unitId;
    private Integer tenantId;

    @Getter
    @Setter
    public static class BuildiumPropertyRef {
        private int id;
    }
}
