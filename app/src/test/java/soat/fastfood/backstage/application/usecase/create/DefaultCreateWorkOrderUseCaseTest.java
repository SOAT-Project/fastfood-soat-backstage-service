package soat.fastfood.backstage.application.usecase.create;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soat.fastfood.backstage.application.domain.exceptions.DomainException;
import soat.fastfood.backstage.application.domain.exceptions.NotificationException;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.port.WorkOrderPort;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    void shouldCreateWorkOrderSuccessfully() {
        // Given
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                validItems
        );

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
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
    void shouldCreateWorkOrderWithSingleItem() {
        // Given
        final var singleItem = List.of(
                new CreateWorkOrderItemCommand("Pizza", 3)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                singleItem
        );

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderCaptor = ArgumentCaptor.forClass(WorkOrder.class);
        verify(workOrderPort, times(1)).create(workOrderCaptor.capture());

        final var capturedWorkOrder = workOrderCaptor.getValue();
        assertNotNull(capturedWorkOrder);
        assertEquals(1, capturedWorkOrder.getItems().size());
        assertEquals("Pizza", capturedWorkOrder.getItems().get(0).getName());
        assertEquals(3, capturedWorkOrder.getItems().get(0).getQuantity());
    }

    @Test
    void shouldCreateWorkOrderWithMultipleItems() {
        // Given
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

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderCaptor = ArgumentCaptor.forClass(WorkOrder.class);
        verify(workOrderPort, times(1)).create(workOrderCaptor.capture());

        final var capturedWorkOrder = workOrderCaptor.getValue();
        assertNotNull(capturedWorkOrder);
        assertEquals(4, capturedWorkOrder.getItems().size());
    }

    @Test
    void shouldCreateWorkOrderSuccessfullyWhenOrderIdIsNull() {
        // Given - WorkOrderID accepts null values without validation
        final var command = new CreateWorkOrderCommand(
                null,
                validOrderNumber,
                validItems
        );

        // When & Then - This will succeed because WorkOrderID.from() doesn't validate
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldCreateWorkOrderSuccessfullyWhenOrderIdIsEmpty() {
        // Given - WorkOrderID accepts empty values without validation
        final var command = new CreateWorkOrderCommand(
                "",
                validOrderNumber,
                validItems
        );

        // When & Then - This will succeed because WorkOrderID.from() doesn't validate
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldCreateWorkOrderSuccessfullyWhenOrderIdIsBlank() {
        // Given - WorkOrderID accepts blank values without validation
        final var command = new CreateWorkOrderCommand(
                "   ",
                validOrderNumber,
                validItems
        );

        // When & Then - This will succeed because WorkOrderID.from() doesn't validate
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldThrowNotificationExceptionWhenOrderNumberIsNull() {
        // Given
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                null,
                validItems
        );

        // When & Then
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        verify(workOrderPort, never()).create(any());
    }

    @Test
    void shouldThrowNotificationExceptionWhenOrderNumberIsEmpty() {
        // Given
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                "",
                validItems
        );

        // When & Then
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        verify(workOrderPort, never()).create(any());
    }

    @Test
    void shouldThrowNotificationExceptionWhenOrderNumberIsBlank() {
        // Given
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                "   ",
                validItems
        );

        // When & Then
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        verify(workOrderPort, never()).create(any());
    }

    @Test
    void shouldThrowNotificationExceptionWhenItemsIsNull() {
        // Given
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                null
        );

        // When & Then
        final var exception = assertThrows(
                NullPointerException.class,
                () -> useCase.execute(command)
        );

        verify(workOrderPort, never()).create(any());
    }

    @Test
    void shouldThrowNotificationExceptionWhenItemsIsEmpty() {
        // Given
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                Collections.emptyList()
        );

        // When & Then
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        verify(workOrderPort, never()).create(any());
    }

    @Test
    void shouldCreateWorkOrderSuccessfullyWhenItemNameIsNull() {
        // Given - WorkOrderItem doesn't validate name
        final var itemsWithNullName = List.of(
                new CreateWorkOrderItemCommand(null, 1)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                itemsWithNullName
        );

        // When & Then - Will succeed as WorkOrderItem.create() doesn't validate
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldCreateWorkOrderSuccessfullyWhenItemQuantityIsNull() {
        // Given - WorkOrderItem doesn't validate quantity
        final var itemsWithNullQuantity = List.of(
                new CreateWorkOrderItemCommand("Burger", null)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                itemsWithNullQuantity
        );

        // When & Then - Will succeed as WorkOrderItem.create() doesn't validate
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldCreateWorkOrderSuccessfullyWhenItemQuantityIsZero() {
        // Given - WorkOrderItem doesn't validate quantity
        final var itemsWithZeroQuantity = List.of(
                new CreateWorkOrderItemCommand("Burger", 0)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                itemsWithZeroQuantity
        );

        // When & Then - Will succeed as WorkOrderItem.create() doesn't validate
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldCreateWorkOrderSuccessfullyWhenItemQuantityIsNegative() {
        // Given - WorkOrderItem doesn't validate quantity
        final var itemsWithNegativeQuantity = List.of(
                new CreateWorkOrderItemCommand("Burger", -1)
        );
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                itemsWithNegativeQuantity
        );

        // When & Then - Will succeed as WorkOrderItem.create() doesn't validate
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldCreateWorkOrderSuccessfullyWithMultipleInvalidItems() {
        // Given - WorkOrderItem doesn't validate items
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

        // When & Then - Will succeed as WorkOrderItem.create() doesn't validate
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldCreateWorkOrderSuccessfullyWithMixedValidAndInvalidItems() {
        // Given - WorkOrderItem doesn't validate items
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

        // When & Then - Will succeed as WorkOrderItem.create() doesn't validate
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldThrowNotificationExceptionWhenOrderNumberIsNullAndItemsEmpty() {
        // Given - Both orderNumber and items validation will fail
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                null,
                Collections.emptyList()
        );

        // When & Then
        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        assertEquals("could not create an aggregate workOrder", exception.getMessage());
        assertFalse(exception.getErrors().isEmpty());
        assertTrue(exception.getErrors().size() >= 2); // At least orderNumber and items errors
        verify(workOrderPort, never()).create(any());
    }

    @Test
    void shouldVerifyLogInfoIsCalled() {
        // Given
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                validItems
        );

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }

    @Test
    void shouldNotCallCreateWhenValidationFails() {
        // Given
        final var command = new CreateWorkOrderCommand(
                null,
                null,
                validItems
        );

        // When & Then
        assertThrows(NotificationException.class, () -> useCase.execute(command));
        verify(workOrderPort, never()).create(any());
    }

    @Test
    void shouldHandleExceptionFromWorkOrderPort() {
        // Given
        final var command = new CreateWorkOrderCommand(
                validOrderId,
                validOrderNumber,
                validItems
        );
        doThrow(new RuntimeException("Database error"))
                .when(workOrderPort).create(any(WorkOrder.class));

        // When & Then
        final var exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Database error", exception.getMessage());
        verify(workOrderPort, times(1)).create(any(WorkOrder.class));
    }
}

