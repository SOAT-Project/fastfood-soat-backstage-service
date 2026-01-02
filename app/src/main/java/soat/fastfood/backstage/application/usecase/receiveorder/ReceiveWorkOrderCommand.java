package soat.fastfood.backstage.application.usecase.receiveorder;

import java.util.List;

public record ReceiveWorkOrderCommand(String id, String orderNumber, List<ReceiveWorkOrderItemCommand> items) {
}
