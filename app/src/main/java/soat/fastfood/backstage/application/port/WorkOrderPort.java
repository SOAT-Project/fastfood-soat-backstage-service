package soat.fastfood.backstage.application.port;

import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;

import java.util.List;
import java.util.Optional;

public interface WorkOrderPort {

    void create(WorkOrder workOrder);

    Optional<WorkOrder> findById(WorkOrderID workOrderID);

    List<WorkOrder> findAllByStatus(WorkOrderStatus status);

    void updateStatus(WorkOrderID workOrderID, WorkOrderStatus status);

    void deleteById(WorkOrderID workOrderID);

}
