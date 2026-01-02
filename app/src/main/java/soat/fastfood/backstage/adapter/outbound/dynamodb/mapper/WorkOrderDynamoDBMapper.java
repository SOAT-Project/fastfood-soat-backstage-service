package soat.fastfood.backstage.adapter.outbound.dynamodb.mapper;

import soat.fastfood.backstage.adapter.outbound.dynamodb.model.WorkOrderDynamoDB;
import soat.fastfood.backstage.adapter.outbound.dynamodb.model.WorkOrderItemDynamoDB;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderItem;

import java.util.List;

public final class WorkOrderDynamoDBMapper {

    private WorkOrderDynamoDBMapper() {
    }

    public static WorkOrderDynamoDB fromDomain(final WorkOrder workOrder) {
        final var workOrderDynamoDB = new WorkOrderDynamoDB();
        workOrderDynamoDB.setId(workOrder.getId().getValue());
        workOrderDynamoDB.setOrderNumber(workOrderDynamoDB.getOrderNumber());
        workOrderDynamoDB.setStatus(workOrder.getStatus().name());
        workOrderDynamoDB.setCreatedAt(workOrder.getCreatedAt().toString());
        workOrderDynamoDB.setUpdatedAt(workOrder.getUpdatedAt().toString());
        workOrderDynamoDB.setItems(fromDomainItems(workOrder.getItems()));
        return workOrderDynamoDB;
    }

    public static List<WorkOrderItemDynamoDB> fromDomainItems(final List<WorkOrderItem> items) {
        return items.stream()
                .map(WorkOrderDynamoDBMapper::fromDomain)
                .toList();
    }

    public static WorkOrderItemDynamoDB fromDomain(final WorkOrderItem workOrderItem) {
        final var workOrderItemDynamoDB = new WorkOrderItemDynamoDB();
        workOrderItemDynamoDB.setName(workOrderItem.getName());
        workOrderItemDynamoDB.setQuantity(workOrderItem.getQuantity());
        return workOrderItemDynamoDB;
    }
}
