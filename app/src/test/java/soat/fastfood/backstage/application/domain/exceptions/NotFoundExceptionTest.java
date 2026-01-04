package soat.fastfood.backstage.application.domain.exceptions;

import org.junit.jupiter.api.Test;
import soat.fastfood.backstage.application.domain.AggregateRoot;
import soat.fastfood.backstage.application.domain.Identifier;
import soat.fastfood.backstage.application.domain.validation.Error;
import soat.fastfood.backstage.application.domain.workorder.WorkOrder;
import soat.fastfood.backstage.application.domain.workorder.WorkOrderID;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void shouldCreateNotFoundExceptionWithAggregateAndId() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var id = WorkOrderID.from("work-order-123");

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("workorder"));
        assertTrue(exception.getMessage().contains("work-order-123"));
        assertTrue(exception.getMessage().contains("was not found"));
    }

    @Test
    void shouldCreateNotFoundExceptionWithError() {
        // Given
        final var error = new Error("Resource not found");

        // When
        final var exception = NotFoundException.with(error);

        // Then
        assertNotNull(exception);
        assertEquals("Resource not found", exception.getMessage());
        assertNotNull(exception.getErrors());
        assertEquals(1, exception.getErrors().size());
        assertEquals("Resource not found", exception.getErrors().get(0).message());
    }

    @Test
    void shouldFormatMessageWithAggregateName() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var id = WorkOrderID.from("abc-123");

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        assertEquals("workorder with id abc-123 was not found", exception.getMessage());
    }

    @Test
    void shouldBeInstanceOfDomainException() {
        // Given
        final var error = new Error("Not found");

        // When
        final var exception = NotFoundException.with(error);

        // Then
        assertInstanceOf(DomainException.class, exception);
    }

    @Test
    void shouldCreateWithEmptyErrorsList() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var id = WorkOrderID.from("test-id");

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        assertNotNull(exception.getErrors());
        assertTrue(exception.getErrors().isEmpty());
    }

    @Test
    void shouldCreateWithSingleErrorInList() {
        // Given
        final var error = new Error("Entity not found");

        // When
        final var exception = NotFoundException.with(error);

        // Then
        assertEquals(1, exception.getErrors().size());
    }

    @Test
    void shouldHandleDifferentIdentifierValues() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var id1 = WorkOrderID.from("id-001");
        final var id2 = WorkOrderID.from("id-002");
        final var id3 = WorkOrderID.from("id-003");

        // When
        final var exception1 = NotFoundException.with(aggregateClass, id1);
        final var exception2 = NotFoundException.with(aggregateClass, id2);
        final var exception3 = NotFoundException.with(aggregateClass, id3);

        // Then
        assertTrue(exception1.getMessage().contains("id-001"));
        assertTrue(exception2.getMessage().contains("id-002"));
        assertTrue(exception3.getMessage().contains("id-003"));
    }

    @Test
    void shouldLowercaseAggregateClassName() {
        // Given
        final var aggregateClass = WorkOrder.class; // "WorkOrder" should become "workorder"
        final var id = WorkOrderID.from("test-id");

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        assertTrue(exception.getMessage().contains("workorder"));
        assertFalse(exception.getMessage().contains("WorkOrder"));
    }

    @Test
    void shouldCreateWithDifferentErrorMessages() {
        // Given
        final var error1 = new Error("User not found");
        final var error2 = new Error("Order not found");
        final var error3 = new Error("Product not found");

        // When
        final var exception1 = NotFoundException.with(error1);
        final var exception2 = NotFoundException.with(error2);
        final var exception3 = NotFoundException.with(error3);

        // Then
        assertEquals("User not found", exception1.getMessage());
        assertEquals("Order not found", exception2.getMessage());
        assertEquals("Product not found", exception3.getMessage());
    }

    @Test
    void shouldHandleEmptyStringInError() {
        // Given
        final var error = new Error("");

        // When
        final var exception = NotFoundException.with(error);

        // Then
        assertEquals("", exception.getMessage());
        assertEquals(1, exception.getErrors().size());
    }

    @Test
    void shouldHandleSpecialCharactersInId() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var id = WorkOrderID.from("test-@#$-123");

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        assertTrue(exception.getMessage().contains("test-@#$-123"));
    }

    @Test
    void shouldHandleLongIdValue() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var longId = "a".repeat(100);
        final var id = WorkOrderID.from(longId);

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        assertTrue(exception.getMessage().contains(longId));
    }

    @Test
    void shouldHandleUUIDFormatId() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var uuid = "550e8400-e29b-41d4-a716-446655440000";
        final var id = WorkOrderID.from(uuid);

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        assertTrue(exception.getMessage().contains(uuid));
        assertEquals("workorder with id " + uuid + " was not found", exception.getMessage());
    }

    @Test
    void shouldPreserveErrorMessageExactly() {
        // Given
        final var errorMessage = "Custom error message with specific format: ID=123, Type=XYZ";
        final var error = new Error(errorMessage);

        // When
        final var exception = NotFoundException.with(error);

        // Then
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldCreateMultipleIndependentExceptions() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var id1 = WorkOrderID.from("id-1");
        final var id2 = WorkOrderID.from("id-2");

        // When
        final var exception1 = NotFoundException.with(aggregateClass, id1);
        final var exception2 = NotFoundException.with(aggregateClass, id2);

        // Then
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
        assertTrue(exception1.getMessage().contains("id-1"));
        assertTrue(exception2.getMessage().contains("id-2"));
    }

    @Test
    void shouldHandleErrorWithSpecialCharacters() {
        // Given
        final var errorMessage = "Error: <tag>value</tag> & special @#$%";
        final var error = new Error(errorMessage);

        // When
        final var exception = NotFoundException.with(error);

        // Then
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldFormatMessageCorrectlyWithAllComponents() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var id = WorkOrderID.from("12345");

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        final var expectedMessage = "workorder with id 12345 was not found";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldReturnEmptyListWhenCreatedWithAggregate() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var id = WorkOrderID.from("test-id");

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        assertEquals(0, exception.getErrors().size());
    }

    @Test
    void shouldReturnSingleItemListWhenCreatedWithError() {
        // Given
        final var error = new Error("Test error");

        // When
        final var exception = NotFoundException.with(error);

        // Then
        assertEquals(1, exception.getErrors().size());
        assertEquals("Test error", exception.getErrors().get(0).message());
    }

    @Test
    void shouldHandleNullIdValue() {
        // Given
        final var aggregateClass = WorkOrder.class;
        final var id = WorkOrderID.from(null);

        // When
        final var exception = NotFoundException.with(aggregateClass, id);

        // Then
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("null"));
    }
}

