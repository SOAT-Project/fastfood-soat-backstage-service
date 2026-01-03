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

    public void updateStatus(String id, String newStatus) {
        log.info("Iniciando atualização de status da WorkOrder ID: {} para {}", id, newStatus);

        // 1. Define a chave para busca
        Key key = Key.builder().partitionValue(id).build();

        // 2. Recupera o item atual do DynamoDB
        WorkOrderDynamoDB entity = table.getItem(key);

        if (entity != null) {
            // 3. Modifica apenas os campos necessários
            entity.setStatus(newStatus);

            // Garante que o campo de atualização seja registrado (ISO-8601)
            // Caso sua entidade use String para datas conforme definimos anteriormente
            entity.setUpdatedAt(java.time.Instant.now().toString());

            // 4. Salva de volta no DynamoDB (o updateItem faz o merge/sobrescrita)
            table.updateItem(entity);

            log.info("WorkOrder {} atualizada com sucesso para o status {}", id, newStatus);
        } else {
            log.error("Falha ao atualizar: WorkOrder com ID {} não encontrada.", id);
            // Opcional: throw new RuntimeException("WorkOrder not found");
        }
    }

}