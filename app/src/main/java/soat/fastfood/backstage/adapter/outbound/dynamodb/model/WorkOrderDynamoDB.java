package soat.fastfood.backstage.adapter.outbound.dynamodb.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.List;

@DynamoDbBean
public class WorkOrderDynamoDB {
    private String id;
    private String orderNumber;
    private String status;
    private String createdAt;
    private String updatedAt;
    private List<WorkOrderItemDynamoDB> items;

    public WorkOrderDynamoDB() {
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "StatusCreatedAtIndex")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbSecondarySortKey(indexNames = "StatusCreatedAtIndex")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<WorkOrderItemDynamoDB> getItems() {
        return items;
    }

    public void setItems(List<WorkOrderItemDynamoDB> items) {
        this.items = items;
    }

}