package soat.fastfood.backstage.application.usecase.retrieve.get;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import soat.fastfood.backstage.application.domain.exceptions.NotFoundException;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.port.WorkOrderPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultGetWorkOrderUseCase extends GetWorkOrderUseCase {

    private final WorkOrderPort workOrderPort;

    @Override
    public GetWorkOrderOutput execute(final GetWorkOrderCommand command) {
        final var workOrderID = WorkOrderID.from(command.id());
        log.info("Retrieving work order by id: {}", workOrderID);

        final var retrievedWorkOrder = this.workOrderPort.findById(workOrderID)
                .orElseThrow(() -> NotFoundException.with(WorkOrder.class, workOrderID));

        return GetWorkOrderOutput.from(retrievedWorkOrder);
    }

}
