package soat.fastfood.backstage.application.usecase.retrieve.get;

import soat.fastfood.backstage.application.domain.workorder.WorkOrder;

import java.time.Instant;
import java.util.List;

public record GetWorkOrderOutput(
        String id,
        String orderNumber,
        List<GetWorkOrderItemOutput> items,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static GetWorkOrderOutput from(final WorkOrder workOrder) {


        return new GetWorkOrderOutput(
                workOrder.getId().getValue(),
                workOrder.getOrderNumber(),
                GetWorkOrderItemOutput.from(workOrder.getItems()),
                workOrder.getStatus().name(),
                workOrder.getCreatedAt(),
                workOrder.getUpdatedAt()
        );
    }
}
