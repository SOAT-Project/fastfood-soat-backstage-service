package soat.fastfood.backstage.application.usecase.delete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.port.WorkOrderPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDeleteWorkOrderUseCase extends DeleteWorkOrderUseCase {

    private final WorkOrderPort workOrderPort;

    @Override
    public void execute(final DeleteWorkOrderCommand command) {
        final var workOrderId = WorkOrderID.from(command.id());

        log.info("Deleting work order ID: {}", workOrderId);

        this.workOrderPort.deleteById(workOrderId);

        log.info("Work order ID: {} deleted", workOrderId);
    }

}
