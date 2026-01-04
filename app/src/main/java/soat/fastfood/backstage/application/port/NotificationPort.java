package soat.fastfood.backstage.application.port;

import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;

public interface NotificationPort {
    void sendWorkOrderStatusUpdateNotification(WorkOrderID workOrderId, WorkOrderStatus status);
}
