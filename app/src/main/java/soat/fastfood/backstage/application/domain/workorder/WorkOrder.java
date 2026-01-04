package soat.fastfood.backstage.application.domain.workorder;

import soat.fastfood.backstage.application.domain.AggregateRoot;
import soat.fastfood.backstage.application.domain.exceptions.NotificationException;
import soat.fastfood.backstage.application.domain.utils.InstantUtils;
import soat.fastfood.backstage.application.domain.validation.ValidationHandler;
import soat.fastfood.backstage.application.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class WorkOrder extends AggregateRoot<WorkOrderID> {

    private final String orderNumber;
    private final Instant createdAt;
    private final List<WorkOrderItem> items;

    private WorkOrderStatus status;
    private Instant updatedAt;

    private WorkOrder(
            final WorkOrderID workOrderID,
            final String orderNumber,
            final WorkOrderStatus status,
            final Instant createdAt,
            final Instant updatedAt,
            final List<WorkOrderItem> items
    ) {
        super(workOrderID);
        this.orderNumber = orderNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items;
        this.selfValidate();
    }

    public static WorkOrder create(
            final String orderId,
            final String orderNumber,
            final List<WorkOrderItem> items
    ) {
        final var workOrderID = WorkOrderID.from(orderId);
        final var now = InstantUtils.now();
        return new WorkOrder(
                workOrderID,
                orderNumber,
                WorkOrderStatus.RECEIVED,
                now,
                now,
                items
        );
    }

    public static WorkOrder with(
            final WorkOrderID workOrderID,
            final String orderNumber,
            final WorkOrderStatus status,
            final Instant createdAt,
            final Instant updatedAt,
            final List<WorkOrderItem> items
    ) {
        return new WorkOrder(
                workOrderID,
                orderNumber,
                status,
                createdAt,
                updatedAt,
                items
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new WorkOrderValidator(this, handler).validate();
    }

    public WorkOrder updateStatus(final WorkOrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<WorkOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("failed to create an aggregate workOrder", notification);
        }
    }

}
