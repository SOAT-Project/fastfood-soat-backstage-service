package soat.fastfood.backstage.application.usecase.retrieve.list;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("List Work Orders Use Case")
class DefaultListWorkOrderUseCaseTest {

    @Mock
    private WorkOrderPort workOrderPort;

    @InjectMocks
    private DefaultListWorkOrderUseCase useCase;

    private WorkOrder createWorkOrderWithItems(String orderId, String orderNumber, List<WorkOrderItem> items) {
        return WorkOrder.create(orderId, orderNumber, items);
    }

    @Nested
    @DisplayName("Given work orders with RECEIVED status exist")
    class GivenWorkOrdersWithReceivedStatusExist {

        @Test
        @DisplayName("When listing by RECEIVED status, Then should return all received work orders")
        void whenListingByReceivedStatus_thenShouldReturnAllReceivedWorkOrders() {
            // Given: multiple work orders with RECEIVED status exist
            final var items1 = List.of(
                    WorkOrderItem.create("Burger", 2),
                    WorkOrderItem.create("Fries", 1)
            );
            final var items2 = List.of(WorkOrderItem.create("Pizza", 1));
            final var workOrder1 = createWorkOrderWithItems("order-1", "ORD-001", items1);
            final var workOrder2 = createWorkOrderWithItems("order-2", "ORD-002", items2);
            final var command = new ListWorkOrderCommand("RECEIVED");

            when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                    .thenReturn(List.of(workOrder1, workOrder2));

            // When: listing work orders by RECEIVED status
            final var result = useCase.execute(command);

            // Then: should return all received work orders with correct data
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("ORD-001", result.get(0).orderNumber());
            assertEquals("ORD-002", result.get(1).orderNumber());
            assertEquals("RECEIVED", result.get(0).status());

            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.RECEIVED);
        }

        @Test
        @DisplayName("When listing with single work order, Then should return list with one item")
        void whenListingWithSingleWorkOrder_thenShouldReturnListWithOneItem() {
            // Given: only one work order with RECEIVED status exists
            final var items = List.of(WorkOrderItem.create("Burger", 2));
            final var workOrder = createWorkOrderWithItems("order-1", "ORD-001", items);
            final var command = new ListWorkOrderCommand("RECEIVED");

            when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                    .thenReturn(List.of(workOrder));

            // When: listing work orders
            final var result = useCase.execute(command);

            // Then: should return single work order
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("ORD-001", result.get(0).orderNumber());
        }

        @Test
        @DisplayName("When converting status string to enum, Then should use correct RECEIVED enum value")
        void whenConvertingStatusStringToEnum_thenShouldUseCorrectReceivedEnumValue() {
            // Given: a command with RECEIVED status string
            final var command = new ListWorkOrderCommand("RECEIVED");

            when(workOrderPort.findAllByStatus(any(WorkOrderStatus.class)))
                    .thenReturn(Collections.emptyList());

            // When: executing command
            useCase.execute(command);

            // Then: should call port with correct enum value
            verify(workOrderPort).findAllByStatus(argThat(status ->
                    status == WorkOrderStatus.RECEIVED
            ));
        }
    }

    @Nested
    @DisplayName("Given work orders with PREPARING status exist")
    class GivenWorkOrdersWithPreparingStatusExist {

        @Test
        @DisplayName("When listing by PREPARING status, Then should return all preparing work orders")
        void whenListingByPreparingStatus_thenShouldReturnAllPreparingWorkOrders() {
            // Given: work orders with PREPARING status exist
            final var items = List.of(WorkOrderItem.create("Burger", 2));
            final var workOrder = createWorkOrderWithItems("order-1", "ORD-001", items);
            final var command = new ListWorkOrderCommand("PREPARING");

            when(workOrderPort.findAllByStatus(WorkOrderStatus.PREPARING))
                    .thenReturn(List.of(workOrder));

            // When: listing by PREPARING status
            final var result = useCase.execute(command);

            // Then: should return preparing work orders
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("ORD-001", result.get(0).orderNumber());

            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.PREPARING);
        }

        @Test
        @DisplayName("When calling port, Then should use correct PREPARING status")
        void whenCallingPort_thenShouldUseCorrectPreparingStatus() {
            // Given: a command with PREPARING status
            final var command = new ListWorkOrderCommand("PREPARING");

            when(workOrderPort.findAllByStatus(any(WorkOrderStatus.class)))
                    .thenReturn(Collections.emptyList());

            // When: executing command
            useCase.execute(command);

            // Then: should call port with PREPARING status and no more interactions
            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.PREPARING);
            verifyNoMoreInteractions(workOrderPort);
        }
    }

    @Nested
    @DisplayName("Given work orders with READY status exist")
    class GivenWorkOrdersWithReadyStatusExist {

        @Test
        @DisplayName("When listing by READY status, Then should return all ready work orders")
        void whenListingByReadyStatus_thenShouldReturnAllReadyWorkOrders() {
            // Given: work orders with READY status exist
            final var items2 = List.of(WorkOrderItem.create("Pizza", 1));
            final var items3 = List.of(
                    WorkOrderItem.create("Salad", 2),
                    WorkOrderItem.create("Juice", 2)
            );
            final var workOrder2 = createWorkOrderWithItems("order-2", "ORD-002", items2);
            final var workOrder3 = createWorkOrderWithItems("order-3", "ORD-003", items3);
            final var command = new ListWorkOrderCommand("READY");

            when(workOrderPort.findAllByStatus(WorkOrderStatus.READY))
                    .thenReturn(List.of(workOrder2, workOrder3));

            // When: listing by READY status
            final var result = useCase.execute(command);

            // Then: should return all ready work orders
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("ORD-002", result.get(0).orderNumber());
            assertEquals("ORD-003", result.get(1).orderNumber());

            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.READY);
        }
    }

    @Nested
    @DisplayName("Given work orders with COMPLETED status exist")
    class GivenWorkOrdersWithCompletedStatusExist {

        @Test
        @DisplayName("When listing by COMPLETED status, Then should return all completed work orders")
        void whenListingByCompletedStatus_thenShouldReturnAllCompletedWorkOrders() {
            // Given: work orders with COMPLETED status exist
            final var items1 = List.of(WorkOrderItem.create("Burger", 2));
            final var items2 = List.of(WorkOrderItem.create("Pizza", 1));
            final var items3 = List.of(WorkOrderItem.create("Salad", 2));
            final var workOrder1 = createWorkOrderWithItems("order-1", "ORD-001", items1);
            final var workOrder2 = createWorkOrderWithItems("order-2", "ORD-002", items2);
            final var workOrder3 = createWorkOrderWithItems("order-3", "ORD-003", items3);
            final var command = new ListWorkOrderCommand("COMPLETED");

            when(workOrderPort.findAllByStatus(WorkOrderStatus.COMPLETED))
                    .thenReturn(List.of(workOrder1, workOrder2, workOrder3));

            // When: listing by COMPLETED status
            final var result = useCase.execute(command);

            // Then: should return all completed work orders
            assertNotNull(result);
            assertEquals(3, result.size());

            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("Given no work orders exist")
    class GivenNoWorkOrdersExist {

        @Test
        @DisplayName("When listing by status, Then should return empty list")
        void whenListingByStatus_thenShouldReturnEmptyList() {
            // Given: no work orders exist for RECEIVED status
            final var command = new ListWorkOrderCommand("RECEIVED");

            when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                    .thenReturn(Collections.emptyList());

            // When: listing work orders
            final var result = useCase.execute(command);

            // Then: should return empty list
            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.size());

            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.RECEIVED);
        }

        @Test
        @DisplayName("When use case is created, Then should not interact with port")
        void whenUseCaseIsCreated_thenShouldNotInteractWithPort() {
            // Given: use case is created but not executed

            // Then: should not have any interaction with port
            verifyNoInteractions(workOrderPort);
        }
    }

    @Nested
    @DisplayName("Given work orders with multiple items")
    class GivenWorkOrdersWithMultipleItems {

        @Test
        @DisplayName("When listing, Then should return work orders with all items")
        void whenListing_thenShouldReturnWorkOrdersWithAllItems() {
            // Given: work orders with different number of items exist
            final var items1 = List.of(
                    WorkOrderItem.create("Burger", 2),
                    WorkOrderItem.create("Fries", 1)
            );
            final var items2 = List.of(WorkOrderItem.create("Pizza", 1));
            final var items3 = List.of(
                    WorkOrderItem.create("Salad", 2),
                    WorkOrderItem.create("Juice", 2)
            );
            final var workOrder1 = createWorkOrderWithItems("order-1", "ORD-001", items1);
            final var workOrder2 = createWorkOrderWithItems("order-2", "ORD-002", items2);
            final var workOrder3 = createWorkOrderWithItems("order-3", "ORD-003", items3);
            final var command = new ListWorkOrderCommand("RECEIVED");

            when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                    .thenReturn(List.of(workOrder1, workOrder2, workOrder3));

            // When: listing work orders
            final var result = useCase.execute(command);

            // Then: should return all work orders with correct item counts
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(2, result.get(0).items().size()); // workOrder1 has 2 items
            assertEquals(1, result.get(1).items().size()); // workOrder2 has 1 item
            assertEquals(2, result.get(2).items().size()); // workOrder3 has 2 items
        }

        @Test
        @DisplayName("When listing large quantity, Then should handle correctly")
        void whenListingLargeQuantity_thenShouldHandleCorrectly() {
            // Given: a large list of work orders exists
            final var items1 = List.of(WorkOrderItem.create("Burger", 2));
            final var items2 = List.of(WorkOrderItem.create("Pizza", 1));
            final var items3 = List.of(WorkOrderItem.create("Salad", 2));
            final var workOrder1 = createWorkOrderWithItems("order-1", "ORD-001", items1);
            final var workOrder2 = createWorkOrderWithItems("order-2", "ORD-002", items2);
            final var workOrder3 = createWorkOrderWithItems("order-3", "ORD-003", items3);
            final var command = new ListWorkOrderCommand("RECEIVED");
            final var largeList = List.of(
                    workOrder1, workOrder2, workOrder3,
                    workOrder1, workOrder2, workOrder3,
                    workOrder1, workOrder2, workOrder3
            );

            when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                    .thenReturn(largeList);

            // When: listing work orders
            final var result = useCase.execute(command);

            // Then: should return all work orders
            assertNotNull(result);
            assertEquals(9, result.size());

            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.RECEIVED);
        }
    }

    @Nested
    @DisplayName("Given output mapping scenarios")
    class GivenOutputMappingScenarios {

        @Test
        @DisplayName("When mapping work orders to output, Then should map all fields correctly")
        void whenMappingWorkOrdersToOutput_thenShouldMapAllFieldsCorrectly() {
            // Given: a work order with complete information
            final var items = List.of(
                    WorkOrderItem.create("Burger", 2),
                    WorkOrderItem.create("Fries", 1)
            );
            final var workOrder = createWorkOrderWithItems("order-1", "ORD-001", items);
            final var command = new ListWorkOrderCommand("RECEIVED");

            when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                    .thenReturn(List.of(workOrder));

            // When: listing work orders
            final var result = useCase.execute(command);

            // Then: all output fields should be mapped correctly
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
        @DisplayName("When executing multiple times, Then should return new list instances")
        void whenExecutingMultipleTimes_thenShouldReturnNewListInstances() {
            // Given: a work order exists
            final var items = List.of(WorkOrderItem.create("Burger", 2));
            final var workOrder = createWorkOrderWithItems("order-1", "ORD-001", items);
            final var command = new ListWorkOrderCommand("RECEIVED");

            when(workOrderPort.findAllByStatus(WorkOrderStatus.RECEIVED))
                    .thenReturn(List.of(workOrder));

            // When: executing command multiple times
            final var result1 = useCase.execute(command);
            final var result2 = useCase.execute(command);

            // Then: should return different list instances
            assertNotNull(result1);
            assertNotNull(result2);
            assertNotSame(result1, result2);
            assertEquals(result1.size(), result2.size());

            verify(workOrderPort, times(2)).findAllByStatus(WorkOrderStatus.RECEIVED);
        }
    }

    @Nested
    @DisplayName("Given status validation scenarios")
    class GivenStatusValidationScenarios {

        @Test
        @DisplayName("When providing invalid status, Then should throw IllegalArgumentException")
        void whenProvidingInvalidStatus_thenShouldThrowIllegalArgumentException() {
            // Given: a command with invalid status
            final var command = new ListWorkOrderCommand("INVALID_STATUS");

            // When & Then: should throw IllegalArgumentException
            assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));

            verify(workOrderPort, never()).findAllByStatus(any(WorkOrderStatus.class));
        }

        @Test
        @DisplayName("When verifying all status enum values, Then should work for all statuses")
        void whenVerifyingAllStatusEnumValues_thenShouldWorkForAllStatuses() {
            // Given: commands for all status types
            when(workOrderPort.findAllByStatus(any(WorkOrderStatus.class)))
                    .thenReturn(Collections.emptyList());

            // When: testing RECEIVED
            final var commandReceived = new ListWorkOrderCommand("RECEIVED");
            useCase.execute(commandReceived);

            // When: testing PREPARING
            final var commandPreparing = new ListWorkOrderCommand("PREPARING");
            useCase.execute(commandPreparing);

            // When: testing READY
            final var commandReady = new ListWorkOrderCommand("READY");
            useCase.execute(commandReady);

            // When: testing COMPLETED
            final var commandCompleted = new ListWorkOrderCommand("COMPLETED");
            useCase.execute(commandCompleted);

            // Then: should have called port with each status exactly once
            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.RECEIVED);
            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.PREPARING);
            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.READY);
            verify(workOrderPort, times(1)).findAllByStatus(WorkOrderStatus.COMPLETED);
        }
    }
}

