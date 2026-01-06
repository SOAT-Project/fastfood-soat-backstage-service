package soat.fastfood.backstage.application.domain.workorder;

import soat.fastfood.backstage.application.domain.validation.Error;
import soat.fastfood.backstage.application.domain.validation.ValidationHandler;
import soat.fastfood.backstage.application.domain.validation.Validator;

import static java.util.Objects.isNull;

public class WorkOrderValidator extends Validator {

    private final WorkOrder workOrder;

    public WorkOrderValidator(final WorkOrder workOrder, final ValidationHandler aHandler) {
        super(aHandler);
        this.workOrder = workOrder;
    }

    @Override
    public void validate() {
        this.validateWorkOrderID();
        this.validateOrderNumber();
        this.validateItems();
        this.validateStatus();
        this.validateCreatedAt();
        this.validateUpdatedAt();
    }

    private void validateWorkOrderID() {
        final var workOrderID = this.workOrder.getId();
        if (isNull(workOrderID))
            this.validationHandler().append(new Error("'workOrderID' should not be null"));
    }

    private void validateOrderNumber() {
        final var orderNumber = this.workOrder.getOrderNumber();
        if (isNull(orderNumber) || orderNumber.isBlank())
            this.validationHandler().append(new Error("'orderNumber' should not be null or empty"));
    }

    private void validateItems() {
        final var items = this.workOrder.getItems();
        if (isNull(items) || items.isEmpty())
            this.validationHandler().append(new Error("'items' should not be null or empty"));
    }

    private void validateStatus() {
        final var status = this.workOrder.getStatus();
        if (isNull(status))
            this.validationHandler().append(new Error("'status' should not be null"));
    }

    private void validateCreatedAt() {
        final var createdAt = this.workOrder.getCreatedAt();
        if (isNull(createdAt)) {
            this.validationHandler().append(new Error("'createdAt' should not be null"));
            return;
        }

        if (createdAt.isAfter(this.workOrder.getUpdatedAt()))
            this.validationHandler().append(new Error("'createdAt' should not be after 'updatedAt'"));
    }

    private void validateUpdatedAt() {
        final var updatedAt = this.workOrder.getUpdatedAt();
        if (isNull(updatedAt)) {
            this.validationHandler().append(new Error("'updatedAt' should not be null"));
            return;
        }

        if (updatedAt.isBefore(this.workOrder.getCreatedAt()))
            this.validationHandler().append(new Error("'updatedAt' should not be before 'createdAt'"));
    }

}
