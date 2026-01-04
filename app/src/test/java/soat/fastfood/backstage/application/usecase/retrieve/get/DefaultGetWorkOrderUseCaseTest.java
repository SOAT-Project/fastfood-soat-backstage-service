package soat.fastfood.backstage.application.usecase.retrieve.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soat.fastfood.backstage.application.domain.exceptions.NotFoundException;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderItem;
import soat.fastfood.backstage.application.port.WorkOrderPort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultGetWorkOrderUseCaseTest {

    @Mock
    private WorkOrderPort workOrderPort;

    @InjectMocks
    private DefaultGetWorkOrderUseCase useCase;

    private String validWorkOrderId;
    private WorkOrder validWorkOrder;

    @BeforeEach
    void setUp() {
        validWorkOrderId = "work-order-123";
        
        final var items = List.of(
                WorkOrderItem.create("Burger", 2),
                WorkOrderItem.create("Fries", 1)
        );
        
        validWorkOrder = WorkOrder.create(
                "order-456",
                "ORD-001",
                items
        );
    }

    @Test
    void shouldRetrieveWorkOrderSuccessfully() {
        // Given
        final var command = new GetWorkOrderCommand(validWorkOrderId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.of(validWorkOrder));

        // When
        final var output = useCase.execute(command);

        // Then
        assertNotNull(output);
        assertEquals("ORD-001", output.orderNumber());
        assertEquals(2, output.items().size());
        assertEquals("Burger", output.items().get(0).name());
        assertEquals(2, output.items().get(0).quantity());
        assertEquals("RECEIVED", output.status());
        assertNotNull(output.createdAt());
        assertNotNull(output.updatedAt());
        
        verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenWorkOrderDoesNotExist() {
        // Given
        final var command = new GetWorkOrderCommand(validWorkOrderId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.empty());

        // When & Then
        final var exception = assertThrows(NotFoundException.class, 
                () -> useCase.execute(command));
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("workorder"));
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
    }

    @Test
    void shouldRetrieveWorkOrderWithDifferentId() {
        // Given
        final var differentId = "different-id-789";
        final var command = new GetWorkOrderCommand(differentId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.of(validWorkOrder));

        // When
        final var output = useCase.execute(command);

        // Then
        assertNotNull(output);
        assertEquals("ORD-001", output.orderNumber());
        verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
    }

    @Test
    void shouldRetrieveWorkOrderWithSingleItem() {
        // Given
        final var items = List.of(WorkOrderItem.create("Pizza", 1));
        final var workOrder = WorkOrder.create("order-123", "ORD-002", items);

        final var command = new GetWorkOrderCommand(validWorkOrderId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.of(workOrder));

        // When
        final var output = useCase.execute(command);

        // Then
        assertNotNull(output);
        assertEquals(1, output.items().size());
        assertEquals("Pizza", output.items().get(0).name());
        assertEquals(1, output.items().get(0).quantity());
    }

    @Test
    void shouldRetrieveWorkOrderWithMultipleItems() {
        // Given
        final var items = List.of(
                WorkOrderItem.create("Burger", 2),
                WorkOrderItem.create("Fries", 3),
                WorkOrderItem.create("Soda", 1),
                WorkOrderItem.create("Salad", 2)
        );
        final var workOrder = WorkOrder.create("order-999", "ORD-003", items);

        final var command = new GetWorkOrderCommand(validWorkOrderId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.of(workOrder));

        // When
        final var output = useCase.execute(command);

        // Then
        assertNotNull(output);
        assertEquals(4, output.items().size());
        assertEquals("Burger", output.items().get(0).name());
        assertEquals("Salad", output.items().get(3).name());
    }

    @Test
    void shouldThrowNotFoundExceptionWithNonExistentId() {
        // Given
        final var nonExistentId = "non-existent-id";
        final var command = new GetWorkOrderCommand(nonExistentId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> useCase.execute(command));
        verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
    }

    @Test
    void shouldCallPortWithCorrectWorkOrderId() {
        // Given
        final var command = new GetWorkOrderCommand(validWorkOrderId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.of(validWorkOrder));

        // When
        useCase.execute(command);

        // Then
        verify(workOrderPort, times(1)).findById(argThat(id -> 
                id.getValue().equals(validWorkOrderId)
        ));
    }

    @Test
    void shouldReturnCorrectOutputFields() {
        // Given
        final var command = new GetWorkOrderCommand(validWorkOrderId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.of(validWorkOrder));

        // When
        final var output = useCase.execute(command);

        // Then
        assertNotNull(output.id());
        assertNotNull(output.orderNumber());
        assertNotNull(output.items());
        assertNotNull(output.status());
        assertNotNull(output.createdAt());
        assertNotNull(output.updatedAt());
    }

    @Test
    void shouldThrowNotFoundExceptionWithEmptyOptional() {
        // Given
        final var command = new GetWorkOrderCommand("any-id");
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.empty());

        // When & Then
        final var exception = assertThrows(NotFoundException.class, 
                () -> useCase.execute(command));
        
        assertNotNull(exception.getMessage());
        verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
    }

    @Test
    void shouldNotInteractWithPortBeforeExecution() {
        // Given - useCase is created but execute is not called yet

        // Then
        verifyNoInteractions(workOrderPort);
    }

    @Test
    void shouldRetrieveWorkOrderWithUUIDFormat() {
        // Given
        final var uuidId = "550e8400-e29b-41d4-a716-446655440000";
        final var command = new GetWorkOrderCommand(uuidId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.of(validWorkOrder));

        // When
        final var output = useCase.execute(command);

        // Then
        assertNotNull(output);
        verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
    }

    @Test
    void shouldMapWorkOrderToOutputCorrectly() {
        // Given
        final var command = new GetWorkOrderCommand(validWorkOrderId);
        when(workOrderPort.findById(any(WorkOrderID.class)))
                .thenReturn(Optional.of(validWorkOrder));

        // When
        final var output = useCase.execute(command);

        // Then
        assertEquals(validWorkOrder.getOrderNumber(), output.orderNumber());
        assertEquals(validWorkOrder.getStatus().name(), output.status());
        assertEquals(validWorkOrder.getItems().size(), output.items().size());
        assertEquals(validWorkOrder.getCreatedAt(), output.createdAt());
        assertEquals(validWorkOrder.getUpdatedAt(), output.updatedAt());
    }
}

