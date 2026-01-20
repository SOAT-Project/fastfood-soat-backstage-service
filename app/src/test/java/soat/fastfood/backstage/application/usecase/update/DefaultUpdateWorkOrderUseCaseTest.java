package soat.fastfood.backstage.application.usecase.update;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("Update Work Order Use Case")
class DefaultUpdateWorkOrderUseCaseTest {

    @Mock
    private WorkOrderPort workOrderPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private DefaultUpdateWorkOrderUseCase useCase;

    @Nested
    @DisplayName("Given a valid work order update request")
    class GivenValidWorkOrderUpdateRequest {

        @Test
        @DisplayName("When updating status to RECEIVED, Then should update and notify successfully")
        void whenUpdatingStatusToReceived_thenShouldUpdateAndNotifySuccessfully() {
            // Given: a command to update status to RECEIVED
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "RECEIVED");

            // When: executing the update
            assertDoesNotThrow(() -> useCase.execute(command));

            // Then: should update port and send notification
            verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.RECEIVED));
            verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.RECEIVED));
        }

        @Test
        @DisplayName("When updating status to PREPARING, Then should update and notify successfully")
        void whenUpdatingStatusToPreparing_thenShouldUpdateAndNotifySuccessfully() {
            // Given: a command to update status to PREPARING
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "PREPARING");

            // When: executing the update
            assertDoesNotThrow(() -> useCase.execute(command));

            // Then: should update port and send notification
            verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.PREPARING));
            verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.PREPARING));
        }

        @Test
        @DisplayName("When updating status to READY, Then should update and notify successfully")
        void whenUpdatingStatusToReady_thenShouldUpdateAndNotifySuccessfully() {
            // Given: a command to update status to READY
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "READY");

            // When: executing the update
            assertDoesNotThrow(() -> useCase.execute(command));

            // Then: should update port and send notification
            verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.READY));
            verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.READY));
        }

        @Test
        @DisplayName("When updating status to COMPLETED, Then should update and notify successfully")
        void whenUpdatingStatusToCompleted_thenShouldUpdateAndNotifySuccessfully() {
            // Given: a command to update status to COMPLETED
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "COMPLETED");

            // When: executing the update
            assertDoesNotThrow(() -> useCase.execute(command));

            // Then: should update port and send notification
            verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.COMPLETED));
            verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.COMPLETED));
        }

        @Test
        @DisplayName("When updating all possible statuses, Then should handle all correctly")
        void whenUpdatingAllPossibleStatuses_thenShouldHandleAllCorrectly() {
            // Given: commands for all possible statuses
            final var receivedCommand = new UpdateWorkOrderCommand("id-1", "RECEIVED");
            final var preparingCommand = new UpdateWorkOrderCommand("id-2", "PREPARING");
            final var readyCommand = new UpdateWorkOrderCommand("id-3", "READY");
            final var completedCommand = new UpdateWorkOrderCommand("id-4", "COMPLETED");

            // When: updating to all statuses
            useCase.execute(receivedCommand);
            useCase.execute(preparingCommand);
            useCase.execute(readyCommand);
            useCase.execute(completedCommand);

            // Then: should have updated each status once
            verify(workOrderPort).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.RECEIVED));
            verify(workOrderPort).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.PREPARING));
            verify(workOrderPort).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.READY));
            verify(workOrderPort).updateStatus(any(WorkOrderID.class), eq(WorkOrderStatus.COMPLETED));

            verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.RECEIVED));
            verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.PREPARING));
            verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.READY));
            verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), eq(WorkOrderStatus.COMPLETED));
        }
    }

    @Nested
    @DisplayName("Given ports interaction sequence")
    class GivenPortsInteractionSequence {

        @Test
        @DisplayName("When executing update, Then should call ports in correct order")
        void whenExecutingUpdate_thenShouldCallPortsInCorrectOrder() {
            // Given: a valid update command
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "PREPARING");
            final var inOrder = inOrder(workOrderPort, notificationPort);

            // When: executing the update
            useCase.execute(command);

            // Then: should call workOrderPort first, then notificationPort
            inOrder.verify(workOrderPort).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
            inOrder.verify(notificationPort).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
        }

        @Test
        @DisplayName("When executing update, Then should call each port exactly once")
        void whenExecutingUpdate_thenShouldCallEachPortExactlyOnce() {
            // Given: a valid update command
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "READY");

            // When: executing the update
            useCase.execute(command);

            // Then: should call each port exactly once
            verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
            verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
            verifyNoMoreInteractions(workOrderPort, notificationPort);
        }

        @Test
        @DisplayName("When use case is created, Then should not interact with ports")
        void whenUseCaseIsCreated_thenShouldNotInteractWithPorts() {
            // Given: use case is created but not executed

            // Then: should not have any interaction with ports
            verifyNoInteractions(workOrderPort);
            verifyNoInteractions(notificationPort);
        }
    }

    @Nested
    @DisplayName("Given parameter conversion scenarios")
    class GivenParameterConversionScenarios {

        @Test
        @DisplayName("When converting command id to WorkOrderID, Then should preserve value")
        void whenConvertingCommandIdToWorkOrderId_thenShouldPreserveValue() {
            // Given: a command with specific id
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "RECEIVED");

            // When: executing the command
            useCase.execute(command);

            // Then: should convert id to WorkOrderID correctly
            final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
            verify(workOrderPort).updateStatus(workOrderIdCaptor.capture(), any(WorkOrderStatus.class));

            final var capturedWorkOrderId = workOrderIdCaptor.getValue();
            assertNotNull(capturedWorkOrderId);
            assertEquals(workOrderId, capturedWorkOrderId.getValue());
        }

        @Test
        @DisplayName("When converting status string to enum, Then should use correct enum value")
        void whenConvertingStatusStringToEnum_thenShouldUseCorrectEnumValue() {
            // Given: a command with PREPARING status string
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "PREPARING");

            // When: executing the command
            useCase.execute(command);

            // Then: should convert to correct enum value
            final var statusCaptor = ArgumentCaptor.forClass(WorkOrderStatus.class);
            verify(workOrderPort).updateStatus(any(WorkOrderID.class), statusCaptor.capture());

            final var capturedStatus = statusCaptor.getValue();
            assertNotNull(capturedStatus);
            assertEquals(WorkOrderStatus.PREPARING, capturedStatus);
        }

        @Test
        @DisplayName("When updating with UUID format, Then should handle correctly")
        void whenUpdatingWithUuidFormat_thenShouldHandleCorrectly() {
            // Given: a command with UUID format id
            final var uuidId = "550e8400-e29b-41d4-a716-446655440000";
            final var command = new UpdateWorkOrderCommand(uuidId, "COMPLETED");

            // When: executing the command
            assertDoesNotThrow(() -> useCase.execute(command));

            // Then: should preserve UUID value
            final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
            verify(workOrderPort).updateStatus(workOrderIdCaptor.capture(), any(WorkOrderStatus.class));
            assertEquals(uuidId, workOrderIdCaptor.getValue().getValue());
        }
    }

    @Nested
    @DisplayName("Given notification scenarios")
    class GivenNotificationScenarios {

        @Test
        @DisplayName("When sending notification, Then should pass correct parameters")
        void whenSendingNotification_thenShouldPassCorrectParameters() {
            // Given: a valid update command
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "READY");

            // When: executing the command
            useCase.execute(command);

            // Then: should send notification with correct parameters
            final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
            final var statusCaptor = ArgumentCaptor.forClass(WorkOrderStatus.class);

            verify(notificationPort).sendWorkOrderStatusUpdateNotification(
                    workOrderIdCaptor.capture(),
                    statusCaptor.capture()
            );

            assertEquals(workOrderId, workOrderIdCaptor.getValue().getValue());
            assertEquals(WorkOrderStatus.READY, statusCaptor.getValue());
        }

        @Test
        @DisplayName("When verifying same parameters to both ports, Then should match")
        void whenVerifyingSameParametersToBothPorts_thenShouldMatch() {
            // Given: a valid update command
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "PREPARING");

            // When: executing the command
            useCase.execute(command);

            // Then: both ports should receive same parameters
            verify(workOrderPort).updateStatus(
                    argThat(id -> id.getValue().equals(workOrderId)),
                    argThat(status -> status == WorkOrderStatus.PREPARING)
            );

            verify(notificationPort).sendWorkOrderStatusUpdateNotification(
                    argThat(id -> id.getValue().equals(workOrderId)),
                    argThat(status -> status == WorkOrderStatus.PREPARING)
            );
        }
    }

    @Nested
    @DisplayName("Given multiple work orders updates")
    class GivenMultipleWorkOrdersUpdates {

        @Test
        @DisplayName("When updating different work orders, Then should handle all independently")
        void whenUpdatingDifferentWorkOrders_thenShouldHandleAllIndependently() {
            // Given: commands for different work orders
            final var id1 = "work-order-1";
            final var id2 = "work-order-2";
            final var id3 = "work-order-3";

            final var command1 = new UpdateWorkOrderCommand(id1, "RECEIVED");
            final var command2 = new UpdateWorkOrderCommand(id2, "PREPARING");
            final var command3 = new UpdateWorkOrderCommand(id3, "READY");

            // When: updating all work orders
            useCase.execute(command1);
            useCase.execute(command2);
            useCase.execute(command3);

            // Then: should have updated all three work orders
            verify(workOrderPort, times(3)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
            verify(notificationPort, times(3)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
        }
    }

    @Nested
    @DisplayName("Given invalid update scenarios")
    class GivenInvalidUpdateScenarios {

        @Test
        @DisplayName("When providing invalid status, Then should throw IllegalArgumentException")
        void whenProvidingInvalidStatus_thenShouldThrowIllegalArgumentException() {
            // Given: a command with invalid status
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "INVALID_STATUS");

            // When & Then: should throw IllegalArgumentException
            assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));

            verify(workOrderPort, never()).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
            verify(notificationPort, never()).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
        }

        @Test
        @DisplayName("When updating with null id, Then should process without throwing exception")
        void whenUpdatingWithNullId_thenShouldProcessWithoutThrowingException() {
            // Given: a command with null id
            final var command = new UpdateWorkOrderCommand(null, "RECEIVED");

            // When: executing the command
            assertDoesNotThrow(() -> useCase.execute(command));

            // Then: should still call ports
            verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
            verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
        }

        @Test
        @DisplayName("When updating with empty string id, Then should process correctly")
        void whenUpdatingWithEmptyStringId_thenShouldProcessCorrectly() {
            // Given: a command with empty string id
            final var command = new UpdateWorkOrderCommand("", "PREPARING");

            // When: executing the command
            assertDoesNotThrow(() -> useCase.execute(command));

            // Then: should preserve empty value
            final var workOrderIdCaptor = ArgumentCaptor.forClass(WorkOrderID.class);
            verify(workOrderPort).updateStatus(workOrderIdCaptor.capture(), any(WorkOrderStatus.class));
            assertEquals("", workOrderIdCaptor.getValue().getValue());
        }
    }

    @Nested
    @DisplayName("Given error handling scenarios")
    class GivenErrorHandlingScenarios {

        @Test
        @DisplayName("When port update fails, Then should propagate exception and not notify")
        void whenPortUpdateFails_thenShouldPropagateExceptionAndNotNotify() {
            // Given: port will throw exception
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "PREPARING");
            final var expectedException = new RuntimeException("Database error");

            doThrow(expectedException)
                    .when(workOrderPort)
                    .updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));

            // When & Then: should propagate exception
            final var exception = assertThrows(RuntimeException.class, () -> useCase.execute(command));
            assertEquals("Database error", exception.getMessage());

            verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
            verify(notificationPort, never()).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
        }

        @Test
        @DisplayName("When notification fails, Then should propagate exception after update")
        void whenNotificationFails_thenShouldPropagateExceptionAfterUpdate() {
            // Given: notification port will throw exception
            final var workOrderId = "work-order-123";
            final var command = new UpdateWorkOrderCommand(workOrderId, "PREPARING");
            final var expectedException = new RuntimeException("Notification service error");

            doThrow(expectedException)
                    .when(notificationPort)
                    .sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));

            // When & Then: should propagate exception
            final var exception = assertThrows(RuntimeException.class, () -> useCase.execute(command));
            assertEquals("Notification service error", exception.getMessage());

            verify(workOrderPort, times(1)).updateStatus(any(WorkOrderID.class), any(WorkOrderStatus.class));
            verify(notificationPort, times(1)).sendWorkOrderStatusUpdateNotification(any(WorkOrderID.class), any(WorkOrderStatus.class));
        }
    }
}

