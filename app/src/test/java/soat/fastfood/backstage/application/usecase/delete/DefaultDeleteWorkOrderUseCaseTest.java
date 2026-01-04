package soat.fastfood.backstage.application.usecase.delete;

import org.junit.jupiter.api.BeforeEach;
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
    void shouldDeleteWorkOrderSuccessfully() {
        // Given
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(validWorkOrderId, capturedWorkOrderId.getValue());
    }

    @Test
    void shouldDeleteWorkOrderWithDifferentId() {
        // Given
        final var differentId = "different-work-order-456";
        final var command = new DeleteWorkOrderCommand(differentId);

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(differentId, capturedWorkOrderId.getValue());
    }

    @Test
    void shouldDeleteWorkOrderWithUUIDFormat() {
        // Given
        final var uuidId = "550e8400-e29b-41d4-a716-446655440000";
        final var command = new DeleteWorkOrderCommand(uuidId);

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(uuidId, capturedWorkOrderId.getValue());
    }

    @Test
    void shouldCallDeleteByIdExactlyOnce() {
        // Given
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);

        // When
        useCase.execute(command);

        // Then
        verify(workOrderPort, times(1)).deleteById(any(WorkOrderID.class));
        verifyNoMoreInteractions(workOrderPort);
    }

    @Test
    void shouldConvertCommandIdToWorkOrderID() {
        // Given
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);

        // When
        useCase.execute(command);

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertInstanceOf(WorkOrderID.class, capturedWorkOrderId);
        assertEquals(validWorkOrderId, capturedWorkOrderId.getValue());
    }

    @Test
    void shouldHandleNullIdFromCommand() {
        // Given
        final var command = new DeleteWorkOrderCommand(null);

        // When & Then
        // The WorkOrderID.from() should handle null - this will test that path
        assertDoesNotThrow(() -> useCase.execute(command));
        verify(workOrderPort, times(1)).deleteById(any(WorkOrderID.class));
    }

    @Test
    void shouldHandleEmptyStringId() {
        // Given
        final var command = new DeleteWorkOrderCommand("");

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals("", capturedWorkOrderId.getValue());
    }

    @Test
    void shouldHandleSpecialCharactersInId() {
        // Given
        final var specialId = "work-order-!@#$%^&*()";
        final var command = new DeleteWorkOrderCommand(specialId);

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(specialId, capturedWorkOrderId.getValue());
    }

    @Test
    void shouldPropagateExceptionWhenPortFails() {
        // Given
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);
        final var expectedException = new RuntimeException("Database error");

        doThrow(expectedException)
                .when(workOrderPort)
                .deleteById(any(WorkOrderID.class));

        // When & Then
        final var exception = assertThrows(RuntimeException.class, () -> useCase.execute(command));
        assertEquals("Database error", exception.getMessage());
        verify(workOrderPort, times(1)).deleteById(any(WorkOrderID.class));
    }

    @Test
    void shouldHandleMultipleDeleteOperations() {
        // Given
        final var command1 = new DeleteWorkOrderCommand("id-1");
        final var command2 = new DeleteWorkOrderCommand("id-2");
        final var command3 = new DeleteWorkOrderCommand("id-3");

        // When
        useCase.execute(command1);
        useCase.execute(command2);
        useCase.execute(command3);

        // Then
        verify(workOrderPort, times(3)).deleteById(any(WorkOrderID.class));
    }

    @Test
    void shouldDeleteWorkOrderWithLongId() {
        // Given
        final var longId = "work-order-" + "a".repeat(100);
        final var command = new DeleteWorkOrderCommand(longId);

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(longId, capturedWorkOrderId.getValue());
    }

    @Test
    void shouldDeleteWorkOrderWithNumericId() {
        // Given
        final var numericId = "12345";
        final var command = new DeleteWorkOrderCommand(numericId);

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(numericId, capturedWorkOrderId.getValue());
    }

    @Test
    void shouldVerifyWorkOrderIdCreation() {
        // Given
        final var command = new DeleteWorkOrderCommand(validWorkOrderId);

        // When
        useCase.execute(command);

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        
        // Verify that the WorkOrderID was created correctly
        assertNotNull(capturedWorkOrderId);
        assertNotNull(capturedWorkOrderId.getValue());
        assertEquals(validWorkOrderId, capturedWorkOrderId.getValue());
    }

    @Test
    void shouldNotInteractWithPortBeforeExecution() {
        // Given - useCase is created but execute is not called yet

        // Then
        verifyNoInteractions(workOrderPort);
    }

    @Test
    void shouldDeleteWorkOrderWithWhitespaceInId() {
        // Given
        final var idWithWhitespace = "work order 123";
        final var command = new DeleteWorkOrderCommand(idWithWhitespace);

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort, times(1)).deleteById(workOrderIdCaptor.capture());

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(idWithWhitespace, capturedWorkOrderId.getValue());
    }
}

