package soat.fastfood.backstage.application.domain.workorder;

import java.util.Arrays;

public enum WorkOrderStatus {
    RECEIVED,
    PREPARING,
    READY,
    DELIVERED;

    public static WorkOrderStatus from(final String status) {
        return Arrays.stream(values())
                .filter(item -> item.name().equals(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid WorkOrderStatus: " + status));
    }

}
