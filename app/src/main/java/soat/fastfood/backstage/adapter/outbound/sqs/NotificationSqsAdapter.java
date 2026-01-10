package soat.fastfood.backstage.adapter.outbound.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import soat.fastfood.backstage.adapter.common.Data;
import soat.fastfood.backstage.adapter.outbound.sqs.dto.WorkOrderStatusNotification;
import soat.fastfood.backstage.application.domain.exceptions.InternalErrorException;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;
import soat.fastfood.backstage.application.port.NotificationPort;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSqsAdapter implements NotificationPort {

    @Value("${message.order-status}")
    private String queue;

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Override
    public void sendWorkOrderStatusUpdateNotification(WorkOrderID workOrderId, WorkOrderStatus status) {
        try {
            final var workOrderStatusNotification = new WorkOrderStatusNotification(
                    workOrderId.getValue(),
                    status.name()
            );

            final var queueUrl = resolveQueueUrl(this.queue);

            final var jsonMessage = this.objectMapper.writeValueAsString(new Data<>(workOrderStatusNotification));

            final var sendRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(jsonMessage)
                    .build();

            final var response = sqsClient.sendMessage(sendRequest);

            log.info("Sent work order status update notification for WorkOrderID: {} with status: {}. MessageId: {}",
                    workOrderId.getValue(),
                    status.name(),
                    response.messageId()
            );
        } catch (JsonProcessingException e) {
            throw InternalErrorException.with("Erro na conversão da mensagem json", e);
        }
    }

    private String resolveQueueUrl(String queueNameOrUrl) {
        if (queueNameOrUrl.startsWith("http") || queueNameOrUrl.startsWith("arn:")) {
            return queueNameOrUrl;
        }
        final var request = GetQueueUrlRequest.builder()
                .queueName(queueNameOrUrl)
                .build();
        return sqsClient.getQueueUrl(request).queueUrl();
    }

}
