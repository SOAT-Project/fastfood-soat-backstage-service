package soat.fastfood.backstage.application.usecase.retrieve.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderItem;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;
import soat.fastfood.backstage.application.port.WorkOrderPort;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultListWorkOrderUseCaseTest {

    @Mock
    private WorkOrderPort workOrderPort;

    @InjectMocks
    private DefaultListWorkOrderUseCase useCase;

    private WorkOrder workOrder1;
    private WorkOrder workOrder2;
    private WorkOrder workOrder3;

    @BeforeEach
    void setUp() {
        final var items1 = List.of(
                WorkOrderItem.create("Burger", 2),
                WorkOrderItem.create("Fries", 1)
        );
        
        final var items2 = List.of(
                WorkOrderItem.create("Pizza", 1)
        );
        
        final var items3 = List.of(
                WorkOrderItem.create("Salad", 2),
                WorkOrderItem.create("Juice", 2)
        );
        
        workOrder1 = WorkOrder.create("order-1", "ORD-001", items1);
        workOrder2 = WorkOrder.create("order-2", "ORD-002", items2);
        workOrder3 = WorkOrder.create("order-3", "ORD-003", items3);
    }

    @Test
    void shouldListWorkOrdersByStatusSuccessfully() {
        // Given
        final var command = new ListWorkOrderCommand("RECEIVED");
        final var workOrders = List.of(workOrder1, workOrder2);
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                .thenReturn(workOrders);

        // When
        final var result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ORD-001", result.get(0).orderNumber());
        assertEquals("ORD-002", result.get(1).orderNumber());
        assertEquals("RECEIVED", result.get(0).status());
        
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.RECEIVED);
    }

    @Test
    void shouldListWorkOrdersByPreparingStatus() {
        // Given
        final var command = new ListWorkOrderCommand("PREPARING");
        final var workOrders = List.of(workOrder1);
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.PREPARING))
                .thenReturn(workOrders);

        // When
        final var result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-001", result.get(0).orderNumber());
        
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.PREPARING);
    }

    @Test
    void shouldListWorkOrdersByReadyStatus() {
        // Given
        final var command = new ListWorkOrderCommand("READY");
        final var workOrders = List.of(workOrder2, workOrder3);
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.READY))
                .thenReturn(workOrders);

        // When
        final var result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ORD-002", result.get(0).orderNumber());
        assertEquals("ORD-003", result.get(1).orderNumber());
        
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.READY);
    }

    @Test
    void shouldListWorkOrdersByDeliveredStatus() {
        // Given
        final var command = new ListWorkOrderCommand("DELIVERED");
        final var workOrders = List.of(workOrder1, workOrder2, workOrder3);
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.DELIVERED))
                .thenReturn(workOrders);

        // When
        final var result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.DELIVERED);
    }

    @Test
    void shouldReturnEmptyListWhenNoWorkOrdersFound() {
        // Given
        final var command = new ListWorkOrderCommand("RECEIVED");
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                .thenReturn(Collections.emptyList());

        // When
        final var result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.RECEIVED);
    }

    @Test
    void shouldMapWorkOrdersToOutputCorrectly() {
        // Given
        final var command = new ListWorkOrderCommand("RECEIVED");
        final var workOrders = List.of(workOrder1);
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                .thenReturn(workOrders);

        // When
        final var result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        final var output = result.get(0);
        assertNotNull(output.id());
        assertNotNull(output.orderNumber());
        assertNotNull(output.items());
        assertNotNull(output.status());
        assertNotNull(output.createdAt());
        assertNotNull(output.updatedAt());
        assertEquals(2, output.items().size());
    }

    @Test
    void shouldHandleMultipleWorkOrdersWithDifferentItems() {
        // Given
        final var command = new ListWorkOrderCommand("RECEIVED");
        final var workOrders = List.of(workOrder1, workOrder2, workOrder3);
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                .thenReturn(workOrders);

        // When
        final var result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(2, result.get(0).items().size()); // workOrder1 has 2 items
        assertEquals(1, result.get(1).items().size()); // workOrder2 has 1 item
        assertEquals(2, result.get(2).items().size()); // workOrder3 has 2 items
    }

    @Test
    void shouldCallPortWithCorrectStatus() {
        // Given
        final var command = new ListWorkOrderCommand("PREPARING");
        
        when(workOrderPort.findAllByStatus(any(WorkOrderStatus.class)))
                .thenReturn(Collections.emptyList());

        // When
        useCase.execute(command);

        // Then
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.PREPARING);
        verifyNoMoreInteractions(workOrderPort);
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {
        // Given
        final var command = new ListWorkOrderCommand("INVALID_STATUS");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        
        verify(workOrderPort, never()).findAllByStatus(any(WorkOrderStatus.class));
    }

    @Test
    void shouldReturnListWithSingleWorkOrder() {
        // Given
        final var command = new ListWorkOrderCommand("RECEIVED");
        final var workOrders = List.of(workOrder1);
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                .thenReturn(workOrders);

        // When
        final var result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-001", result.get(0).orderNumber());
    }

    @Test
    void shouldConvertStatusStringToEnum() {
        // Given
        final var command = new ListWorkOrderCommand("RECEIVED");
        
        when(workOrderPort.findAllByStatus(any(WorkOrderStatus.class)))
                .thenReturn(Collections.emptyList());

        // When
        useCase.execute(command);

        // Then
        verify(workOrderPort).findAllByStatus(argThat(status -> 
                status == WorkOrderStatus.RECEIVED
        ));
    }

    @Test
    void shouldHandleLargeListOfWorkOrders() {
        // Given
        final var command = new ListWorkOrderCommand("RECEIVED");
        final var largeList = List.of(
                workOrder1, workOrder2, workOrder3,
                workOrder1, workOrder2, workOrder3,
                workOrder1, workOrder2, workOrder3
        );
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                .thenReturn(largeList);

        // When
        final var result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(9, result.size());
        
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.RECEIVED);
    }

    @Test
    void shouldNotInteractWithPortBeforeExecution() {
        // Given - useCase is created but execute is not called yet

        // Then
        verifyNoInteractions(workOrderPort);
    }

    @Test
    void shouldReturnNewListInstanceForEachExecution() {
        // Given
        final var command = new ListWorkOrderCommand("RECEIVED");
        final var workOrders = List.of(workOrder1);
        
        when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                .thenReturn(workOrders);

        // When
        final var result1 = useCase.execute(command);
        final var result2 = useCase.execute(command);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertEquals(result1.size(), result2.size());
        
        verify(workOrderPort, times(2)).findAllByStatus(WorkOrderStatus.RECEIVED);
    }

    @Test
    void shouldVerifyAllStatusEnumValues() {
        // Test RECEIVED
        final var commandReceived = new ListWorkOrderCommand("RECEIVED");
        when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                .thenReturn(Collections.emptyList());
        useCase.execute(commandReceived);

        // Test PREPARING
        final var commandPreparing = new ListWorkOrderCommand("PREPARING");
        when(workOrderPort.findAllByStatus(WorkOrderStatus.PREPARING))
                .thenReturn(Collections.emptyList());
        useCase.execute(commandPreparing);

        // Test READY
        final var commandReady = new ListWorkOrderCommand("READY");
        when(workOrderPort.findAllByStatus(WorkOrderStatus.READY))
                .thenReturn(Collections.emptyList());
        useCase.execute(commandReady);

        // Test DELIVERED
        final var commandDelivered = new ListWorkOrderCommand("DELIVERED");
        when(workOrderPort.findAllByStatus(WorkOrderStatus.DELIVERED))
                .thenReturn(Collections.emptyList());
        useCase.execute(commandDelivered);

        // Then
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.RECEIVED);
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.PREPARING);
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.READY);
        verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.DELIVERED);
    }
}

