package soat.fastfood.backstage.adapter.outbound.dynamodb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import soat.fastfood.backstage.adapter.outbound.dynamodb.mapper.WorkOrderDynamoDBMapper;
import soat.fastfood.backstage.adapter.outbound.dynamodb.service.WorkOrderDynamoDBService;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.port.WorkOrderPort;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WorkOrderDynamoDBAdapter implements WorkOrderPort {

    private final WorkOrderDynamoDBService service;

    @Override
    public void create(final WorkOrder workOrder) {
        this.service.saveOrder(WorkOrderDynamoDBMapper.fromDomain(workOrder));
    }

}
