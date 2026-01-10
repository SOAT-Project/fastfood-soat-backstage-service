package soat.fastfood.backstage.application.usecase.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderStatus;
import soat.fastfood.backstage.application.port.NotificationPort;
import soat.fastfood.backstage.application.port.WorkOrderPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultUpdateWorkOrderUseCaseTest {

    @Mock
    private WorkOrderPort workOrderPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private DefaultUpdateWorkOrderUseCase useCase;

    private String validWorkOrderId;

    @BeforeEach
    void setUp() {
        validWorkOrderId = "work-order-123";
    }

    @Test
    void shouldUpdateWorkOrderStatusToReceivedSuccessfully() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "RECEIVED");

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.RECEIVED));
        verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.RECEIVED));
    }

    @Test
    void shouldUpdateWorkOrderStatusToPreparingSuccessfully() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "PREPARING");

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.PREPARING));
        verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.PREPARING));
    }

    @Test
    void shouldUpdateWorkOrderStatusToReadySuccessfully() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "READY");

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.READY));
        verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.READY));
    }

    @Test
    void shouldUpdateWorkOrderStatusToDeliveredSuccessfully() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "COMPLETED");

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.COMPLETED));
        verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.COMPLETED));
    }

    @Test
    void shouldCallBothPortsInCorrectOrder() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "PREPARING");
        final var inOrder = inOrder(workOrderPort, notificationPort);

        // When
        useCase.execute(command);

        // Then
        inOrder.verify(workOrderPort).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
        inOrder.verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
    }

    @Test
    void shouldConvertCommandIdToWorkOrderID() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "RECEIVED");

        // When
        useCase.execute(command);

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort).updateStatus(workOrderIdCaptor.capture(), any(WorkOrderStatus.class));

        final var capturedWorkOrderId = workOrderIdCaptor.getValue();
        assertNotNull(capturedWorkOrderId);
        assertEquals(validWorkOrderId, capturedWorkOrderId.getValue());
    }

    @Test
    void shouldConvertStatusStringToEnum() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "PREPARING");

        // When
        useCase.execute(command);

        // Then
        final var statusCaptor = ArgumentCaptor.forClass(WorkOrderStatus.class);
        verify(workOrderPort).updateStatus(any(WorkOrderID.class), statusCaptor.capture());

        final var capturedStatus = statusCaptor.getValue();
        assertNotNull(capturedStatus);
        assertEquals(WorkOrderStatus.PREPARING, capturedStatus);
    }

    @Test
    void shouldSendNotificationWithCorrectParameters() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "READY");

        // When
        useCase.execute(command);

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        final var statusCaptor = ArgumentCaptor.forClass(WorkOrderStatus.class);
        
        verify(notificationPort).sendWorkOrderStatusUpdateNotification(
                workOrderIdCaptor.capture(), 
                statusCaptor.capture()
        );

        assertEquals(validWorkOrderId, workOrderIdCaptor.getValue().getValue());
        assertEquals(WorkOrderStatus.READY, statusCaptor.getValue());
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "INVALID_STATUS");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        
        verify(workOrderPort, never()).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
        verify(notificationPort, never()).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
    }

    @Test
    void shouldUpdateWorkOrderWithDifferentIds() {
        // Given
        final var id1 = "work-order-1";
        final var id2 = "work-order-2";
        final var id3 = "work-order-3";

        final var command1 = new UpdateWorkOrderCommand(id1, "RECEIVED");
        final var command2 = new UpdateWorkOrderCommand(id2, "PREPARING");
        final var command3 = new UpdateWorkOrderCommand(id3, "READY");

        // When
        useCase.execute(command1);
        useCase.execute(command2);
        useCase.execute(command3);

        // Then
        verify(workOrderPort, times(3)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
        verify(notificationPort, times(3)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
    }

    @Test
    void shouldPropagateExceptionWhenPortFails() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "PREPARING");
        final var expectedException = new RuntimeException("Database error");

        doThrow(expectedException)
                .when(workOrderPort)
                .updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));

        // When & Then
        final var exception = assertThrows(RuntimeException.class, () -> useCase.execute(command));
        assertEquals("Database error", exception.getMessage());
        
        verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
        verify(notificationPort, never()).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
    }

    @Test
    void shouldPropagateExceptionWhenNotificationFails() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "PREPARING");
        final var expectedException = new RuntimeException("Notification service error");

        doThrow(expectedException)
                .when(notificationPort)
                .sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));

        // When & Then
        final var exception = assertThrows(RuntimeException.class, () -> useCase.execute(command));
        assertEquals("Notification service error", exception.getMessage());
        
        verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
        verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
    }

    @Test
    void shouldNotInteractWithPortsBeforeExecution() {
        // Given - useCase is created but execute is not called yet

        // Then
        verifyNoInteractions(workOrderPort);
        verifyNoInteractions(notificationPort);
    }

    @Test
    void shouldUpdateWorkOrderWithUUIDFormat() {
        // Given
        final var uuidId = "550e8400-e29b-41d4-a716-446655440000";
        final var command = new UpdateWorkOrderCommand(uuidId, "COMPLETED");

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort).updateStatus(workOrderIdCaptor.capture(), any(WorkOrderStatus.class));
        assertEquals(uuidId, workOrderIdCaptor.getValue().getValue());
    }

    @Test
    void shouldCallPortsExactlyOnce() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "READY");

        // When
        useCase.execute(command);

        // Then
        verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
        verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
        verifyNoMoreInteractions(workOrderPort, notificationPort);
    }

    @Test
    void shouldHandleNullIdFromCommand() {
        // Given
        final var command = new UpdateWorkOrderCommand(null, "RECEIVED");

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
        verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
    }

    @Test
    void shouldHandleEmptyStringId() {
        // Given
        final var command = new UpdateWorkOrderCommand("", "PREPARING");

        // When
        assertDoesNotThrow(() -> useCase.execute(command));

        // Then
        final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
        verify(workOrderPort).updateStatus(workOrderIdCaptor.capture(), any(WorkOrderStatus.class));
        assertEquals("", workOrderIdCaptor.getValue().getValue());
    }

    @Test
    void shouldUpdateToAllPossibleStatuses() {
        // Test all enum values
        final var receivedCommand = new UpdateWorkOrderCommand("id-1", "RECEIVED");
        final var preparingCommand = new UpdateWorkOrderCommand("id-2", "PREPARING");
        final var readyCommand = new UpdateWorkOrderCommand("id-3", "READY");
        final var deliveredCommand = new UpdateWorkOrderCommand("id-4", "COMPLETED");

        // When
        useCase.execute(receivedCommand);
        useCase.execute(preparingCommand);
        useCase.execute(readyCommand);
        useCase.execute(deliveredCommand);

        // Then
        verify(workOrderPort).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.RECEIVED));
        verify(workOrderPort).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.PREPARING));
        verify(workOrderPort).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.READY));
        verify(workOrderPort).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.COMPLETED));
        
        verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.RECEIVED));
        verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.PREPARING));
        verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.READY));
        verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.COMPLETED));
    }

    @Test
    void shouldVerifyWorkOrderIdAndStatusPassedToBothPorts() {
        // Given
        final var command = new UpdateWorkOrderCommand(validWorkOrderId, "PREPARING");

        // When
        useCase.execute(command);

        // Then
        verify(workOrderPort).updateStatus(
                argThat(id -> id.getValue().equals(validWorkOrderId)),
                argThat(status -> status == WorkOrderStatus.PREPARING)
        );
        
        verify(notificationPort).sendWorkOrderStatusUpdateNotification(
                argThat(id -> id.getValue().equals(validWorkOrderId)),
                argThat(status -> status == WorkOrderStatus.PREPARING)
        );
    }
}

