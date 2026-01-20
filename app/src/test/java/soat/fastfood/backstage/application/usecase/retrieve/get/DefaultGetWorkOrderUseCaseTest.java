package soat.fastfood.backstage.application.usecase.retrieve.get;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("Get Work Order Use Case")
class DefaultGetWorkOrderUseCaseTest {

    @Mock
    private WorkOrderPort workOrderPort;

    @InjectMocks
    private DefaultGetWorkOrderUseCase useCase;

    @Nested
    @DisplayName("Given a valid work order exists")
    class GivenValidWorkOrderExists {

        @Test
        @DisplayName("When retrieving by id, Then should return work order successfully")
        void whenRetrievingById_thenShouldReturnWorkOrderSuccessfully() {
            // Given: a valid work order exists in the system
            final var workOrderId = "work-order-123";
            final var items = List.of(
                    WorkOrderItem.create("Burger", 2),
                    WorkOrderItem.create("Fries", 1)
            );
            final var workOrder = WorkOrder.create("order-456", "ORD-001", items);
            final var command = new GetWorkOrderCommand(workOrderId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.of(workOrder));

            // When: retrieving the work order
            final var output = useCase.execute(command);

            // Then: should return complete work order information
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
        @DisplayName("When retrieving by different id, Then should return correct work order")
        void whenRetrievingByDifferentId_thenShouldReturnCorrectWorkOrder() {
            // Given: a work order with different id exists
            final var differentId = "different-id-789";
            final var items = List.of(WorkOrderItem.create("Pizza", 1));
            final var workOrder = WorkOrder.create("order-456", "ORD-001", items);
            final var command = new GetWorkOrderCommand(differentId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.of(workOrder));

            // When: retrieving by this id
            final var output = useCase.execute(command);

            // Then: should return the correct work order
            assertNotNull(output);
            assertEquals("ORD-001", output.orderNumber());
            verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
        }

        @Test
        @DisplayName("When retrieving with UUID format, Then should handle correctly")
        void whenRetrievingWithUuidFormat_thenShouldHandleCorrectly() {
            // Given: a work order with UUID format id
            final var uuidId = "550e8400-e29b-41d4-a716-446655440000";
            final var items = List.of(WorkOrderItem.create("Burger", 1));
            final var workOrder = WorkOrder.create("order-456", "ORD-001", items);
            final var command = new GetWorkOrderCommand(uuidId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.of(workOrder));

            // When: retrieving with UUID format
            final var output = useCase.execute(command);

            // Then: should retrieve successfully
            assertNotNull(output);
            verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
        }
    }

    @Nested
    @DisplayName("Given a work order with single item")
    class GivenWorkOrderWithSingleItem {

        @Test
        @DisplayName("When retrieving, Then should return work order with one item")
        void whenRetrieving_thenShouldReturnWorkOrderWithOneItem() {
            // Given: a work order with single item exists
            final var workOrderId = "work-order-123";
            final var items = List.of(WorkOrderItem.create("Pizza", 1));
            final var workOrder = WorkOrder.create("order-123", "ORD-002", items);
            final var command = new GetWorkOrderCommand(workOrderId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.of(workOrder));

            // When: retrieving the work order
            final var output = useCase.execute(command);

            // Then: should return work order with single item
            assertNotNull(output);
            assertEquals(1, output.items().size());
            assertEquals("Pizza", output.items().get(0).name());
            assertEquals(1, output.items().get(0).quantity());
        }
    }

    @Nested
    @DisplayName("Given a work order with multiple items")
    class GivenWorkOrderWithMultipleItems {

        @Test
        @DisplayName("When retrieving, Then should return all items correctly")
        void whenRetrieving_thenShouldReturnAllItemsCorrectly() {
            // Given: a work order with multiple items exists
            final var workOrderId = "work-order-123";
            final var items = List.of(
                    WorkOrderItem.create("Burger", 2),
                    WorkOrderItem.create("Fries", 3),
                    WorkOrderItem.create("Soda", 1),
                    WorkOrderItem.create("Salad", 2)
            );
            final var workOrder = WorkOrder.create("order-999", "ORD-003", items);
            final var command = new GetWorkOrderCommand(workOrderId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.of(workOrder));

            // When: retrieving the work order
            final var output = useCase.execute(command);

            // Then: should return all items in order
            assertNotNull(output);
            assertEquals(4, output.items().size());
            assertEquals("Burger", output.items().get(0).name());
            assertEquals("Salad", output.items().get(3).name());
        }
    }

    @Nested
    @DisplayName("Given work order does not exist")
    class GivenWorkOrderDoesNotExist {

        @Test
        @DisplayName("When retrieving, Then should throw NotFoundException")
        void whenRetrieving_thenShouldThrowNotFoundException() {
            // Given: work order does not exist in the system
            final var workOrderId = "work-order-123";
            final var command = new GetWorkOrderCommand(workOrderId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.empty());

            // When & Then: should throw NotFoundException
            final var exception = assertThrows(NotFoundException.class,
                    () -> useCase.execute(command));

            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("workorder"));
            assertTrue(exception.getMessage().contains("not found"));

            verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
        }

        @Test
        @DisplayName("When retrieving with non-existent id, Then should throw exception")
        void whenRetrievingWithNonExistentId_thenShouldThrowException() {
            // Given: a non-existent work order id
            final var nonExistentId = "non-existent-id";
            final var command = new GetWorkOrderCommand(nonExistentId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.empty());

            // When & Then: should throw NotFoundException
            assertThrows(NotFoundException.class, () -> useCase.execute(command));
            verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
        }

        @Test
        @DisplayName("When port returns empty, Then should throw NotFoundException")
        void whenPortReturnsEmpty_thenShouldThrowNotFoundException() {
            // Given: port returns empty optional
            final var command = new GetWorkOrderCommand("any-id");

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.empty());

            // When & Then: should throw NotFoundException with message
            final var exception = assertThrows(NotFoundException.class,
                    () -> useCase.execute(command));

            assertNotNull(exception.getMessage());
            verify(workOrderPort, times(1)).findById(any(WorkOrderID.class));
        }
    }

    @Nested
    @DisplayName("Given port interaction scenarios")
    class GivenPortInteractionScenarios {

        @Test
        @DisplayName("When executing command, Then should call port with correct id")
        void whenExecutingCommand_thenShouldCallPortWithCorrectId() {
            // Given: a valid command with specific id
            final var workOrderId = "work-order-123";
            final var items = List.of(WorkOrderItem.create("Burger", 1));
            final var workOrder = WorkOrder.create("order-456", "ORD-001", items);
            final var command = new GetWorkOrderCommand(workOrderId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.of(workOrder));

            // When: executing the command
            useCase.execute(command);

            // Then: should call port with correct work order id
            verify(workOrderPort, times(1)).findById(argThat(id ->
                    id.getValue().equals(workOrderId)
            ));
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
    @DisplayName("Given output mapping scenarios")
    class GivenOutputMappingScenarios {

        @Test
        @DisplayName("When retrieving, Then should map all output fields correctly")
        void whenRetrieving_thenShouldMapAllOutputFieldsCorrectly() {
            // Given: a work order with complete information
            final var workOrderId = "work-order-123";
            final var items = List.of(WorkOrderItem.create("Burger", 1));
            final var workOrder = WorkOrder.create("order-456", "ORD-001", items);
            final var command = new GetWorkOrderCommand(workOrderId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.of(workOrder));

            // When: retrieving the work order
            final var output = useCase.execute(command);

            // Then: all output fields should be mapped correctly
            assertNotNull(output.id());
            assertNotNull(output.orderNumber());
            assertNotNull(output.items());
            assertNotNull(output.status());
            assertNotNull(output.createdAt());
            assertNotNull(output.updatedAt());
        }

        @Test
        @DisplayName("When mapping domain to output, Then should preserve all data")
        void whenMappingDomainToOutput_thenShouldPreserveAllData() {
            // Given: a work order domain entity
            final var workOrderId = "work-order-123";
            final var items = List.of(WorkOrderItem.create("Burger", 2));
            final var workOrder = WorkOrder.create("order-456", "ORD-001", items);
            final var command = new GetWorkOrderCommand(workOrderId);

            when(workOrderPort.findById(any(WorkOrderID.class)))
                    .thenReturn(Optional.of(workOrder));

            // When: mapping to output
            final var output = useCase.execute(command);

            // Then: should preserve all domain data
            assertEquals(workOrder.getOrderNumber(), output.orderNumber());
            assertEquals(workOrder.getStatus().name(), output.status());
            assertEquals(workOrder.getItems().size(), output.items().size());
            assertEquals(workOrder.getCreatedAt(), output.createdAt());
            assertEquals(workOrder.getUpdatedAt(), output.updatedAt());
        }
    }
}

