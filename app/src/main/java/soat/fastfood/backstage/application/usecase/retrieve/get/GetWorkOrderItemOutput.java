package soat.fastfood.backstage.application.usecase.retrieve.get;

import soat.fastfood.backstage.application.domain.workorder.WorkOrderItem;

import java.util.List;

public record GetWorkOrderItemOutput(
        String name,
        Integer quantity
) {
    public static List<GetWorkOrderItemOutput> from(final List<WorkOrderItem> workOrderItems) {
        return workOrderItems.stream()
                .map(GetWorkOrderItemOutput::from)
                .toList();
    }

    public static GetWorkOrderItemOutput from(final WorkOrderItem workOrderItem) {
        return new GetWorkOrderItemOutput(workOrderItem.getName(), workOrderItem.getQuantity());
    }
}
