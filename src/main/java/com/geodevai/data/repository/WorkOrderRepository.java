package com.geodevai.data.repository;

import com.geodevai.data.model.Person;
import com.geodevai.data.model.Property;
import com.geodevai.data.model.Unit;
import com.geodevai.data.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource(path = "work-order")
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {

    List<WorkOrder> findByProperty(Property property);

    List<WorkOrder> findByUnit(Unit unit);

    List<WorkOrder> findByStatus(String status);

    Optional<WorkOrder> findByExternalWorkOrderId(String externalWorkOrderId);

    List<WorkOrder> findByTenant(Person tenant);
}
