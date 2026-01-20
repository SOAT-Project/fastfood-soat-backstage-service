package soat.fastfood.backstage.application.usecase.create;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soat.fastfood.backstage.application.domain.exceptions.NotificationException;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.port.WorkOrderPort;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Feature: Create Work Order Use Case")
class DefaultCreateWorkOrderUseCaseTest {

    @Mock
    private WorkOrderPort workOrderPort;

    @InjectMocks
    private DefaultCreateWorkOrderUseCase useCase;

    private String validOrderId;
    private String validOrderNumber;
    private List<CreateWorkOrderItemCommand> validItems;

    @BeforeEach
    void setUp() {
        validOrderId = "order-123";
        validOrderNumber = "ORD-001";
        validItems = List.of(
                new CreateWorkOrderItemCommand("Burger", 2),
                new CreateWorkOrderItemCommand("Fries", 1)
        );
    }

    @Test
    @DisplayName("Scenario: Create work order with valid data")
    void givenValidOrderData_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: A valid order with multiple items
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                validItems
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created with correct data
        final var workOrderCaptor = ArgumentCaptor.forClass(WorkOrder.class);
        verify(workOrderPort, times(1)).create(workOrderCaptor.capture());

        final var capturedWorkOrder = workOrderCaptor.getValue();
        assertNotNull(capturedWorkOrder);
        assertEquals(validOrderNumber, capturedWorkOrder.getOrderNumber());
        assertEquals(2, capturedWorkOrder.getItems().size());
        assertEquals("Burger", capturedWorkOrder.getItems().get(0).getName());
        assertEquals(2, capturedWorkOrder.getItems().get(0).getQuantity());
        assertEquals("Fries", capturedWorkOrder.getItems().get(1).getName());
        assertEquals(1, capturedWorkOrder.getItems().get(1).getQuantity());
    }

    @Test
    @DisplayName("Scenario: Create work order with single item")
    void givenOrderWithSingleItem_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with a single item
        final var singleItem = List.of(
                new CreateWorkOrderItemCommand("Pizza", 3)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                singleItem
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created with one item
        final var workOrderCaptor = ArgumentCaptor.forClass(WorkOrder.class);
        verify(workOrderPort, times(1)).create(workOrderCaptor.capture());

        final var capturedWorkOrder = workOrderCaptor.getValue();
        assertNotNull(capturedWorkOrder);
        assertEquals(1, capturedWorkOrder.getItems().size());
        assertEquals("Pizza", capturedWorkOrder.getItems().get(0).getName());
        assertEquals(3, capturedWorkOrder.getItems().get(0).getQuantity());
    }

    @Test
    @DisplayName("Scenario: Create work order with multiple items")
    void givenOrderWithMultipleItems_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with four different items
        final var multipleItems = List.of(
                new CreateWorkOrderItemCommand("Burger", 1),
                new CreateWorkOrderItemCommand("Fries", 2),
                new CreateWorkOrderItemCommand("Soda", 3),
                new CreateWorkOrderItemCommand("Salad", 1)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                multipleItems
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created with all items
        final var workOrderCaptor = ArgumentCaptor.forClass(WorkOrder.class);
        verify(workOrderPort, times(1)).create(workOrderCaptor.capture());

        final var capturedWorkOrder = workOrderCaptor.getValue();
        assertNotNull(capturedWorkOrder);
        assertEquals(4, capturedWorkOrder.getItems().size());
    }

    @Test
    @DisplayName("Scenario: Create work order when order ID is null")
    void givenNullOrderId_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with null ID (WorkOrderID accepts null without validation)
        final var command = new CreateWorkOrderCommand(
                null,
                validOrderNumber,
                validItems
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Create work order when order ID is empty")
    void givenEmptyOrderId_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with empty ID (WorkOrderID accepts empty without validation)
        final var command = new CreateWorkOrderCommand(
                "",
                validOrderNumber,
                validItems
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Create work order when order ID is blank")
    void givenBlankOrderId_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with blank ID (WorkOrderID accepts blank without validation)
        final var command = new CreateWorkOrderCommand(
                "   ",
                validOrderNumber,
                validItems
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Fail to create work order when order number is null")
    void givenNullOrderNumber_whenCreateWorkOrder_thenShouldThrowNotificationException() {
        // Given: An order with null order number
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                null,
                validItems
        );

        // When: Attempting to create the work order
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        // Then: Should throw NotificationException with validation errors
        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        verify(workOrderPort, never()).create(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create work order when order number is empty")
    void givenEmptyOrderNumber_whenCreateWorkOrder_thenShouldThrowNotificationException() {
        // Given: An order with empty order number
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                "",
                validItems
        );

        // When: Attempting to create the work order
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        // Then: Should throw NotificationException with validation errors
        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        verify(workOrderPort, never()).create(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create work order when order number is blank")
    void givenBlankOrderNumber_whenCreateWorkOrder_thenShouldThrowNotificationException() {
        // Given: An order with blank order number
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                "   ",
                validItems
        );

        // When: Attempting to create the work order
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        // Then: Should throw NotificationException with validation errors
        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        verify(workOrderPort, never()).create(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create work order when items list is null")
    void givenNullItemsList_whenCreateWorkOrder_thenShouldThrowNullPointerException() {
        // Given: An order with null items list
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                null
        );

        // When: Attempting to create the work order
        final var exception = assertThrows(
                NullPointerException.class,
                () -> useCase.execute(command)
        );

        // Then: Should throw NullPointerException
        verify(workOrderPort, never()).create(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create work order when items list is empty")
    void givenEmptyItemsList_whenCreateWorkOrder_thenShouldThrowNotificationException() {
        // Given: An order with empty items list
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                Collections.emptyList()
        );

        // When: Attempting to create the work order
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        // Then: Should throw NotificationException with validation errors
        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        verify(workOrderPort, never()).create(any());
    }

    @Test
    @DisplayName("Scenario: Create work order when item name is null")
    void givenItemWithNullName_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with an item that has null name (WorkOrderItem doesn't validate)
        final var itemsWithNullName = List.of(
                new CreateWorkOrderItemCommand(null, 1)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                itemsWithNullName
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Create work order when item quantity is null")
    void givenItemWithNullQuantity_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with an item that has null quantity (WorkOrderItem doesn't validate)
        final var itemsWithNullQuantity = List.of(
                new CreateWorkOrderItemCommand("Burger", null)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                itemsWithNullQuantity
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Create work order when item quantity is zero")
    void givenItemWithZeroQuantity_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with an item that has zero quantity (WorkOrderItem doesn't validate)
        final var itemsWithZeroQuantity = List.of(
                new CreateWorkOrderItemCommand("Burger", 0)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                itemsWithZeroQuantity
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Create work order when item quantity is negative")
    void givenItemWithNegativeQuantity_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with an item that has negative quantity (WorkOrderItem doesn't validate)
        final var itemsWithNegativeQuantity = List.of(
                new CreateWorkOrderItemCommand("Burger", -1)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                itemsWithNegativeQuantity
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Create work order with multiple invalid items")
    void givenMultipleInvalidItems_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with multiple items containing invalid data (WorkOrderItem doesn't validate)
        final var invalidItems = List.of(
                new CreateWorkOrderItemCommand(null, 1),
                new CreateWorkOrderItemCommand("Burger", 0),
                new CreateWorkOrderItemCommand("", -1)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                invalidItems
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Create work order with mixed valid and invalid items")
    void givenMixedValidAndInvalidItems_whenCreateWorkOrder_thenShouldCreateSuccessfully() {
        // Given: An order with both valid and invalid items (WorkOrderItem doesn't validate)
        final var mixedItems = List.of(
                new CreateWorkOrderItemCommand("Burger", 2),
                new CreateWorkOrderItemCommand(null, 1),
                new CreateWorkOrderItemCommand("Fries", 0)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                mixedItems
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be created
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Fail to create work order with multiple validation errors")
    void givenNullOrderNumberAndEmptyItems_whenCreateWorkOrder_thenShouldThrowNotificationException() {
        // Given: An order with both null order number and empty items list
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                null,
                Collections.emptyList()
        );

        // When: Attempting to create the work order
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        // Then: Should throw NotificationException with multiple validation errors
        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        assertTrue(exception.getErrors().size() >= 2);
        verify(workOrderPort, never()).create(any());
    }

    @Test
    @DisplayName("Scenario: Verify work order port is called on successful creation")
    void givenValidOrderData_whenCreateWorkOrder_thenShouldCallWorkOrderPort() {
        // Given: A valid order
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                validItems
        );

        // When: Creating the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order port should be called exactly once
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Scenario: Work order port is not called when validation fails")
    void givenInvalidOrderData_whenCreateWorkOrder_thenShouldNotCallWorkOrderPort() {
        // Given: An order with invalid data (both null)
        final var command = new CreateWorkOrderCommand(
                null,
                null,
                validItems
        );

        // When: Attempting to create the work order
        assertThrows(NotificationException.class, () -> useCase.execute(command));

        // Then: The work order port should not be called
        verify(workOrderPort, never()).create(any());
    }

    @Test
    @DisplayName("Scenario: Handle exception from work order port")
    void givenWorkOrderPortThrowsException_whenCreateWorkOrder_thenShouldPropagateException() {
        // Given: A valid order but the port throws an exception
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                validItems
        );
        doThrow(new RuntimeException("Database error"))
                .when(workOrderPort).create(any(WorkOrder.class));

        // When: Attempting to create the work order
        final var exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        // Then: The exception should be propagated with the correct message
        assertEquals("Database error", exception.getMessage());
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }
}

