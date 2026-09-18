package com.geodevai.integration.buildium;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class BuildiumWorkOrderListResponse {
    private List<BuildiumWorkOrder> workOrders;
}
