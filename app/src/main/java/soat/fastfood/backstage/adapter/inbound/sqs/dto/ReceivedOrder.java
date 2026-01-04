package soat.fastfood.backstage.adapter.inbound.sqs.dto;

import java.util.List;

public record ReceivedOrder(String id, String orderNumber, List<ReceivedOrderItem> items) {
}
