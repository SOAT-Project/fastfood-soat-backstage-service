package soat.fastfood.backstage.adapter.outbound.dynamodb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import soat.fastfood.backstage.adapter.outbound.dynamodb.model.WorkOrderDynamoDB;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkOrderDynamoDBService {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderDynamoDBService.class);
    private final DynamoDbTable<WorkOrderDynamoDB> table;

    public WorkOrderDynamoDBService(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("work-orders", TableSchema.fromBean(WorkOrderDynamoDB.class));
    }

    public void saveOrder(WorkOrderDynamoDB entity) {
        log.debug("Salvando pedido ID: {}", entity.getId());
        table.putItem(entity);
    }

    public Optional<WorkOrderDynamoDB> findById(String id) {
        log.debug("Buscando pedido por ID: {}", id);
        Key key = Key.builder().partitionValue(id).build();
        return Optional.ofNullable(table.getItem(key));
    }

    public List<WorkOrderDynamoDB> findByStatus(String status) {
        log.debug("Consultando GSI StatusCreatedAtIndex para status: {}", status);

        DynamoDbIndex<WorkOrderDynamoDB> index = table.index("StatusCreatedAtIndex");

        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(status).build());

        return index.query(QueryEnhancedRequest.builder()
                        .queryConditional(queryConditional)
                        .build())
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    public List<WorkOrderDynamoDB> findAll() {
        log.debug("Executando Scan na tabela orders");
        return table.scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        log.debug("Deletando pedido ID: {}", id);
        table.deleteItem(Key.builder().partitionValue(id).build());
    }

}