package soat.fastfood.backstage.application.domain.workorder;

import soat.fastfood.backstage.application.domain.ValueObject;

public class WorkOrderItem extends ValueObject {

    private final String name;
    private final Integer quantity;

    private WorkOrderItem(final String name, final Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public static WorkOrderItem create(final String name, final Integer quantity) {
        return new WorkOrderItem(name, quantity);
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

}
