package soat.fastfood.backstage.application.usecase.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;
import soat.fastfood.backstage.application.port.NotificationPort;
import soat.fastfood.backstage.application.port.WorkOrderPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultUpdateWorkOrderUseCase extends UpdateWorkOrderUseCase {

    private final WorkOrderPort workOrderPort;
    private final NotificationPort notificationPort;

    @Override
    public void execute(final UpdateWorkOrderCommand command) {
        final var workOrderId = WorkOrderID.from(command.id());
        final var newWorkOrderStatus = WorkOrderStatus.from(command.status());

        log.info("Updating work order ID: {} to status: {}", workOrderId, newWorkOrderStatus);

        this.workOrderPort.updateStatus(workOrderId, newWorkOrderStatus);
        this.notificationPort.sendWorkOrderStatusUpdateNotification(workOrderId, newWorkOrderStatus);

        log.info("Work order ID: {} updated to status: {}", workOrderId, newWorkOrderStatus);
    }

}
