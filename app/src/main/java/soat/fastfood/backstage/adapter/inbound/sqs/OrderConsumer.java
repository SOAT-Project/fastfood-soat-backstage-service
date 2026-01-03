package soat.fastfood.backstage.adapter.inbound.sqs;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soat.fastfood.backstage.adapter.common.Data;
import soat.fastfood.backstage.adapter.inbound.sqs.dto.ReceivedOrder;
import soat.fastfood.backstage.application.usecase.create.CreateWorkOrderCommand;
import soat.fastfood.backstage.application.usecase.create.CreateWorkOrderItemCommand;
import soat.fastfood.backstage.application.usecase.create.CreateWorkOrderUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final CreateWorkOrderUseCase receiveWorkOrderUseCase;

    @SqsListener("${message.order}")
    public void listen(final Data<ReceivedOrder> order) {
        log.info("Received order message: {}", order);

        final var data = order.data();

        final var command = new CreateWorkOrderCommand(
                data.id(),
                data.orderNumber(),
                data.items().stream()
                        .map(item -> new CreateWorkOrderItemCommand(item.name(), item.quantity()))
                        .toList()
        );

        this.receiveWorkOrderUseCase.execute(command);
    }

}
