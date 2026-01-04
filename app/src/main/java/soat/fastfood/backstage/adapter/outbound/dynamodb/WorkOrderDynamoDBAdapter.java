package soat.fastfood.backstage.adapter.outbound.dynamodb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import soat.fastfood.backstage.adapter.outbound.dynamodb.mapper.WorkOrderDynamoDBMapper;
import soat.fastfood.backstage.adapter.outbound.dynamodb.service.WorkOrderDynamoDBService;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;
import soat.fastfood.backstage.application.port.WorkOrderPort;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WorkOrderDynamoDBAdapter implements WorkOrderPort {

    private final WorkOrderDynamoDBService service;

    @Override
    public void create(final WorkOrder workOrder) {
        this.service.saveOrder(WorkOrderDynamoDBMapper.fromDomain(workOrder));
    }

    @Override
    public Optional<WorkOrder> findById(final WorkOrderID workOrderID) {
        return this.service.findById(workOrderID.getValue()).map(WorkOrderDynamoDBMapper::toDomain);
    }

    @Override
    public List<WorkOrder> findAllByStatus(final WorkOrderStatus status) {
        return this.service.findByStatus(status.name()).stream()
                .map(WorkOrderDynamoDBMapper::toDomain)
                .toList();
    }

    @Override
    public void updateStatus(final WorkOrderID workOrderID, final WorkOrderStatus status) {
        this.service.updateStatus(workOrderID.getValue(), status.name());
    }

    @Override
    public void deleteById(final WorkOrderID workOrderID) {
        this.service.deleteById(workOrderID.getValue());
    }

}
