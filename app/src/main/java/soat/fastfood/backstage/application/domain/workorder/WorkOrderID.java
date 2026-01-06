package soat.fastfood.backstage.application.domain.workorder;

import soat.fastfood.backstage.application.domain.Identifier;

import java.util.Objects;

public class WorkOrderID extends Identifier {

    private String value;

    private WorkOrderID(final String value) {
        this.value = value;
    }

    public static WorkOrderID from(final String value) {
        return new WorkOrderID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final WorkOrderID that = (WorkOrderID) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

}
