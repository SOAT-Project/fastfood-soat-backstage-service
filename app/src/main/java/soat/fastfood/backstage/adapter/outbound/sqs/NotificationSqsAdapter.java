package soat.fastfood.backstage.adapter.outbound.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import soat.fastfood.backstage.adapter.common.Data;
import soat.fastfood.backstage.adapter.outbound.sqs.dto.WorkOrderStatusNotification;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;
import soat.fastfood.backstage.application.port.NotificationPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSqsAdapter implements NotificationPort {

    @Value("${message.order-status}")
    private String queue;

    private final SqsTemplate sqsTemplate;

    @Override
    public void sendWorkOrderStatusUpdateNotification(WorkOrderID workOrderId, WorkOrderStatus status) {
        final var workOrderStatusNotification = new WorkOrderStatusNotification(
                workOrderId.getValue(),
                status.name()
        );

        final var result = this.sqsTemplate.send(to -> to
                .queue(queue)
                .payload(new Data<>(workOrderStatusNotification))
        );

        log.info("Sent work order status update notification for WorkOrderID: {} with status: {}. MessageId: {}",
                workOrderId.getValue(),
                status.name(),
                result.messageId()
        );
    }

}
