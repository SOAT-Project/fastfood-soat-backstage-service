package soat.fastfood.backstage.application.usecase.retrieve.list;

import soat.fastfood.backstage.application.domain.workorder.WorkOrderItem;

import java.util.List;

public record ListWorkOrderItemOutput(
        String name,
        Integer quantity
) {
    public static List<ListWorkOrderItemOutput> from(final List<WorkOrderItem> workOrderItems) {
        return workOrderItems.stream()
                .map(ListWorkOrderItemOutput::from)
                .toList();
    }

    public static ListWorkOrderItemOutput from(final WorkOrderItem workOrderItem) {
        return new ListWorkOrderItemOutput(workOrderItem.getName(), workOrderItem.getQuantity());
    }
}
