package soat.fastfood.backstage.application.usecase.create;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import soat.fastfood.backstage.application.domain.exceptions.NotificationException;
import soat.fastfood.backstage.application.domain.validation.handler.Notification;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderItem;
import soat.fastfood.backstage.application.port.WorkOrderPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCreateWorkOrderUseCase extends CreateWorkOrderUseCase {

    private final WorkOrderPort workOrderPort;

    @Override
    public void execute(final CreateWorkOrderCommand command) {
        final var orderId = command.id();
        final var orderNumber = command.orderNumber();
        final var orderItems = command.items();
        log.info("Recebendo ordem de trabalho ID: {}, orderNumber: {}", orderId, orderNumber);

        final var notification = Notification.create();

        final var workOrderItems = orderItems
                .stream()
                .map(orderItem -> notification.validate(
                        () -> WorkOrderItem.create(orderItem.name(), orderItem.quantity())
                ))
                .toList();

        final var workOrder = notification.validate(() -> WorkOrder.create(
                orderId,
                orderNumber,
                workOrderItems
        ));

        if (notification.hasError())
            throw new NotificationException("could not create an aggregate workOrder", notification);

        this.workOrderPort.create(workOrder);
    }

}
