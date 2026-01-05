package soat.fastfood.backstage.application.usecase.create;

import java.util.List;

public record CreateWorkOrderCommand(String id, String orderNumber, List<CreateWorkOrderItemCommand> items) {
}
