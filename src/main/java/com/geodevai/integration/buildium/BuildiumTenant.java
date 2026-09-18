package com.geodevai.integration.buildium;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class BuildiumTenant {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String[] phoneNumbers;
    private List<BuildiumLease> leases;

    @Getter
    @Setter
    public static class BuildiumLease {
        private String startDate;
        private String endDate;
    }
}
