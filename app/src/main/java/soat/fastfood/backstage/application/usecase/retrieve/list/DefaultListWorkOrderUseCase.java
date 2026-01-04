package soat.fastfood.backstage.application.usecase.retrieve.list;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;
import soat.fastfood.backstage.application.port.WorkOrderPort;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultListWorkOrderUseCase extends ListWorkOrderUseCase {

    private final WorkOrderPort workOrderPort;

    @Override
    public List<ListWorkOrderOutput> execute(final ListWorkOrderCommand command) {
        final var workOrderStatus = WorkOrderStatus.from(command.status());

        log.info("Listing work orders by status: {}", workOrderStatus);

        final var workOrders = this.workOrderPort.findAllByStatus(workOrderStatus);

        log.info("Found {} work orders with status: {}", workOrders.size(), workOrderStatus);

        return workOrders.stream().map(ListWorkOrderOutput::from).toList();
    }

}
