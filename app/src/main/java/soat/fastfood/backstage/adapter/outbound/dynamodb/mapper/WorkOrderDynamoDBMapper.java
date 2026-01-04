package soat.fastfood.backstage.adapter.outbound.dynamodb.mapper;

import soat.fastfood.backstage.adapter.outbound.dynamodb.model.WorkOrderDynamoDB;
import soat.fastfood.backstage.adapter.outbound.dynamodb.model.WorkOrderItemDynamoDB;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderItem;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;

import java.time.Instant;
import java.util.List;

public final class WorkOrderDynamoDBMapper {

    private WorkOrderDynamoDBMapper() {
    }

    public static WorkOrderDynamoDB fromDomain(final WorkOrder workOrder) {
        final var workOrderDynamoDB = new WorkOrderDynamoDB();
        workOrderDynamoDB.setId(workOrder.getId().getValue());
        workOrderDynamoDB.setOrderNumber(workOrder.getOrderNumber());
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

    public static WorkOrder toDomain(final WorkOrderDynamoDB workOrderDynamoDB) {
        try {
            return WorkOrder.with(
                    WorkOrderID.from(workOrderDynamoDB.getId()),
                    workOrderDynamoDB.getOrderNumber(),
                    WorkOrderStatus.from(workOrderDynamoDB.getStatus()),
                    workOrderDynamoDB.getCreatedAt() != null ? Instant.parse(workOrderDynamoDB.getCreatedAt()) : null,
                    workOrderDynamoDB.getUpdatedAt() != null ? Instant.parse(workOrderDynamoDB.getUpdatedAt()) : null,
                    toDomain(workOrderDynamoDB.getItems())
            );
        } catch (final Exception e) {
            throw new IllegalStateException("Error mapping WorkOrderDynamoDB to WorkOrder", e);
        }
    }

    public static List<WorkOrderItem> toDomain(final List<WorkOrderItemDynamoDB> itemsDynamoDB) {
        return itemsDynamoDB.stream()
                .map(WorkOrderDynamoDBMapper::toDomain)
                .toList();
    }

    public static WorkOrderItem toDomain(final WorkOrderItemDynamoDB workOrderItemDynamoDB) {
        return WorkOrderItem.create(
                workOrderItemDynamoDB.getName(),
                workOrderItemDynamoDB.getQuantity()
        );
    }
}
