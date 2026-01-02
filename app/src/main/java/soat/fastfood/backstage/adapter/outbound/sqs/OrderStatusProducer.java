package soat.fastfood.backstage.adapter.outbound.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusProducer {

    @Value("${message.order-status}")
    private String queue;

    private final SqsTemplate sqs;

    public void produceOrderStatusEvent() {
//        sqs.send(to -> to.)
    }
}
