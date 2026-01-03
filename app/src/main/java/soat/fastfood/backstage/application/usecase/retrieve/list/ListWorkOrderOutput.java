package soat.fastfood.backstage.application.usecase.retrieve.list;

import soat.fastfood.backstage.application.domain.workorder.WorkOrder;

import java.time.Instant;
import java.util.List;

public record ListWorkOrderOutput(
        String id,
        String orderNumber,
        List<ListWorkOrderItemOutput> items,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static ListWorkOrderOutput from(final WorkOrder workOrder) {


        return new ListWorkOrderOutput(
                workOrder.getId().getValue(),
                workOrder.getOrderNumber(),
                ListWorkOrderItemOutput.from(workOrder.getItems()),
                workOrder.getStatus().name(),
                workOrder.getCreatedAt(),
                workOrder.getUpdatedAt()
        );
    }
}
