package soat.fastfood.backstage.application.usecase.delete;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.port.WorkOrderPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Feature: Delete Work Order Use Case")
class DefaultDeleteWorkOrderUseCaseTest {

    @Mock
    private WorkOrderPort workOrderPort;

    @InjectMocks
    private DefaultDeleteWorkOrderUseCase useCase;

    private String validWorkOrderId;

    @BeforeEach
    void setUp() {
        validWorkOrderId = "work-order-123";
    }

    @Test
    @DisplayName("Scenario: Delete work order with valid ID")
    void givenValidWorkOrderId_whenDeleteWorkOrder_thenShouldDeleteSuccessfully() {
        // Given: A valid work order ID
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);

        // When: Deleting the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be deleted successfully
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(validWorkOrderId, capturedWorkOrderId.getValue());
    }

    @Test
    @DisplayName("Scenario: Delete work order with different ID format")
    void givenDifferentWorkOrderId_whenDeleteWorkOrder_thenShouldDeleteSuccessfully() {
        // Given: A work order with different ID format
        final var differentId = "different-work-order-456";
        final var command = new DeleteWorkOrderCommand(differentId);

        // When: Deleting the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be deleted with the correct ID
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(differentId, capturedWorkOrderId.getValue());
    }

    @Test
    @DisplayName("Scenario: Delete work order with UUID format ID")
    void givenUUIDFormatId_whenDeleteWorkOrder_thenShouldDeleteSuccessfully() {
        // Given: A work order with UUID format ID
        final var uuidId = "550e8400-e29b-41d4-a716-446655440000";
        final var command = new DeleteWorkOrderCommand(uuidId);

        // When: Deleting the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be deleted with UUID preserved
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(uuidId, capturedWorkOrderId.getValue());
    }

    @Test
    @DisplayName("Scenario: Verify port is called exactly once per deletion")
    void givenValidWorkOrderId_whenDeleteWorkOrder_thenShouldCallPortExactlyOnce() {
        // Given: A valid work order ID
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);

        // When: Deleting the work order
        useCase.execute(command);

        // Then: The port should be called exactly once with no other interactions
        verify(workOrderPort, times(1)).deleteById(any(WorkOrderID.class));
        verifyNoMoreInteractions(workOrderPort);
    }

    @Test
    @DisplayName("Scenario: Convert command ID to WorkOrderID domain object")
    void givenCommandWithId_whenDeleteWorkOrder_thenShouldConvertToWorkOrderID() {
        // Given: A delete command with an ID
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);

        // When: Executing the delete operation
        useCase.execute(command);

        // Then: The ID should be converted to WorkOrderID domain object
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertInstanceOf(WorkOrderID.class, capturedWorkOrderId);
        assertEquals(validWorkOrderId, capturedWorkOrderId.getValue());
    }

    @Test
    @DisplayName("Scenario: Delete work order when ID is null")
    void givenNullWorkOrderId_whenDeleteWorkOrder_thenShouldHandleGracefully() {
        // Given: A command with null ID (WorkOrderID accepts null)
        final var command = new DeleteWorkOrderCommand(null);

        // When: Deleting the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The deletion should proceed without errors
        verify(workOrderPort, times(1)).deleteById(any(WorkOrderID.class));
    }

    @Test
    @DisplayName("Scenario: Delete work order when ID is empty string")
    void givenEmptyStringId_whenDeleteWorkOrder_thenShouldHandleGracefully() {
        // Given: A command with empty string ID
        final var command = new DeleteWorkOrderCommand("");

        // When: Deleting the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The deletion should proceed with empty ID
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals("", capturedWorkOrderId.getValue());
    }

    @Test
    @DisplayName("Scenario: Delete work order with special characters in ID")
    void givenIdWithSpecialCharacters_whenDeleteWorkOrder_thenShouldDeleteSuccessfully() {
        // Given: A work order ID containing special characters
        final var specialId = "work-order-!@#$%^&*()";
        final var command = new DeleteWorkOrderCommand(specialId);

        // When: Deleting the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be deleted with special characters preserved
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(specialId, capturedWorkOrderId.getValue());
    }

    @Test
    @DisplayName("Scenario: Handle exception when port fails during deletion")
    void givenPortThrowsException_whenDeleteWorkOrder_thenShouldPropagateException() {
        // Given: A valid command but the port throws an exception
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);
        final var expectedException = new RuntimeException("Database error");

        doThrow(expectedException)
                .when(workOrderPort)
                .deleteById(any(WorkOrderID.class));

        // When: Attempting to delete the work order
        final var exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(command)
        );

        // Then: The exception should be propagated with the correct message
        assertEquals("Database error", exception.getMessage());
        verify(workOrderPort, times(1)).deleteById(any(WorkOrderID.class));
    }

    @Test
    @DisplayName("Scenario: Handle multiple sequential delete operations")
    void givenMultipleDeleteCommands_whenDeleteWorkOrders_thenShouldDeleteAllSuccessfully() {
        // Given: Multiple delete commands for different work orders
        final var command1 = new DeleteWorkOrderCommand("id-1");
        final var command2 = new DeleteWorkOrderCommand("id-2");
        final var command3 = new DeleteWorkOrderCommand("id-3");

        // When: Executing multiple delete operations
        useCase.execute(command1);
        useCase.execute(command2);
        useCase.execute(command3);

        // Then: All work orders should be deleted (port called three times)
        verify(workOrderPort, times(3)).deleteById(any(WorkOrderID.class));
    }

    @Test
    @DisplayName("Scenario: Delete work order with very long ID")
    void givenLongWorkOrderId_whenDeleteWorkOrder_thenShouldDeleteSuccessfully() {
        // Given: A work order with very long ID (100+ characters)
        final var longId = "work-order-" + "a".repeat(100);
        final var command = new DeleteWorkOrderCommand(longId);

        // When: Deleting the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be deleted with long ID preserved
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(longId, capturedWorkOrderId.getValue());
    }

    @Test
    @DisplayName("Scenario: Delete work order with numeric-only ID")
    void givenNumericWorkOrderId_whenDeleteWorkOrder_thenShouldDeleteSuccessfully() {
        // Given: A work order with numeric-only ID
        final var numericId = "12345";
        final var command = new DeleteWorkOrderCommand(numericId);

        // When: Deleting the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be deleted with numeric ID preserved
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(numericId, capturedWorkOrderId.getValue());
    }

    @Test
    @DisplayName("Scenario: Verify WorkOrderID creation from command")
    void givenValidCommand_whenDeleteWorkOrder_thenShouldCreateWorkOrderIDCorrectly() {
        // Given: A valid delete command
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);

        // When: Executing the delete operation
        useCase.execute(command);

        // Then: WorkOrderID should be created correctly with proper value
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertNotNull(capturedWorkOrderId.getValue());
        assertEquals(validWorkOrderId, capturedWorkOrderId.getValue());
    }

    @Test
    @DisplayName("Scenario: Verify no port interaction before execution")
    void givenUseCaseNotExecuted_whenCheckingPortInteractions_thenShouldHaveNoInteractions() {
        // Given: Use case is created but execute is not called yet

        // When: No action is performed

        // Then: The port should have no interactions
        verifyNoInteractions(workOrderPort);
    }

    @Test
    @DisplayName("Scenario: Delete work order with whitespace in ID")
    void givenIdWithWhitespace_whenDeleteWorkOrder_thenShouldDeleteSuccessfully() {
        // Given: A work order ID containing whitespace
        final var idWithWhitespace = "work order 123";
        final var command = new DeleteWorkOrderCommand(idWithWhitespace);

        // When: Deleting the work order
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then: The work order should be deleted with whitespace preserved
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(idWithWhitespace, capturedWorkOrderId.getValue());
    }
}

