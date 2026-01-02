package soat.fastfood.backstage.application.port;

import soat.fastfood.backstage.application.domain.workorder.WorkOrder;

public interface WorkOrderPort {

    void create(WorkOrder workOrder);

}
