package soat.fastfood.backstage.application.domain.exceptions;

import org.junit.jupiter.api.Test;
import soat.fastfood.backstage.application.domain.validation.Error;
import soat.fastfood.backstage.application.domain.validation.handler.Notification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationExceptionTest {

    @Test
    void shouldCreateNotificationExceptionWithMessageAndNotification() {
        // Given
        final var message = "Validation failed";
        final var notification = Notification.create();
        notification.append(new Error("Error 1"));
        notification.append(new Error("Error 2"));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getErrors());
        assertEquals(2, exception.getErrors().size());
    }

    @Test
    void shouldPreserveErrorsFromNotification() {
        // Given
        final var message = "Business rule violation";
        final var notification = Notification.create();
        notification.append(new Error("Field 'name' is required"));
        notification.append(new Error("Field 'email' is invalid"));
        notification.append(new Error("Field 'age' must be positive"));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals(3, exception.getErrors().size());
        assertEquals("Field 'name' is required", exception.getErrors().get(0).message());
        assertEquals("Field 'email' is invalid", exception.getErrors().get(1).message());
        assertEquals("Field 'age' must be positive", exception.getErrors().get(2).message());
    }

    @Test
    void shouldBeInstanceOfDomainException() {
        // Given
        final var message = "Exception message";
        final var notification = Notification.create();
        notification.append(new Error("Error"));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertInstanceOf(DomainException.class, exception);
    }

    @Test
    void shouldHandleEmptyNotification() {
        // Given
        final var message = "No errors";
        final var notification = Notification.create();

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception.getErrors().isEmpty());
    }

    @Test
    void shouldHandleSingleError() {
        // Given
        final var message = "Single validation error";
        final var notification = Notification.create();
        notification.append(new Error("Single error"));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals(1, exception.getErrors().size());
        assertEquals("Single error", exception.getErrors().get(0).message());
    }

    @Test
    void shouldHandleNullMessage() {
        // Given
        final var notification = Notification.create();
        notification.append(new Error("Error"));

        // When
        final var exception = new NotificationException(null, notification);

        // Then
        assertNull(exception.getMessage());
        assertEquals(1, exception.getErrors().size());
    }

    @Test
    void shouldHandleEmptyMessage() {
        // Given
        final var message = "";
        final var notification = Notification.create();
        notification.append(new Error("Error"));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals("", exception.getMessage());
    }

    @Test
    void shouldCreateMultipleIndependentExceptions() {
        // Given
        final var notification1 = Notification.create();
        notification1.append(new Error("Error A"));

        final var notification2 = Notification.create();
        notification2.append(new Error("Error B"));

        // When
        final var exception1 = new NotificationException("Message 1", notification1);
        final var exception2 = new NotificationException("Message 2", notification2);

        // Then
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
        assertEquals("Error A", exception1.getErrors().get(0).message());
        assertEquals("Error B", exception2.getErrors().get(0).message());
    }

    @Test
    void shouldHandleLongErrorMessage() {
        // Given
        final var longMessage = "This is a very long validation error message that contains " +
                "detailed information about what went wrong during the validation process " +
                "and provides comprehensive feedback to the user.";
        final var notification = Notification.create();
        notification.append(new Error("Error"));

        // When
        final var exception = new NotificationException(longMessage, notification);

        // Then
        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    void shouldHandleSpecialCharactersInMessage() {
        // Given
        final var specialMessage = "Error: @#$%^&*()_+-=[]{}|;':\",./<>?";
        final var notification = Notification.create();
        notification.append(new Error("Error"));

        // When
        final var exception = new NotificationException(specialMessage, notification);

        // Then
        assertEquals(specialMessage, exception.getMessage());
    }

    @Test
    void shouldHandleMultipleErrorsInNotification() {
        // Given
        final var message = "Multiple validation errors";
        final var notification = Notification.create();

        for (int i = 1; i <= 10; i++) {
            notification.append(new Error("Error " + i));
        }

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals(10, exception.getErrors().size());
        for (int i = 0; i < 10; i++) {
            assertEquals("Error " + (i + 1), exception.getErrors().get(i).message());
        }
    }

    @Test
    void shouldPreserveErrorOrder() {
        // Given
        final var message = "Ordered errors";
        final var notification = Notification.create();
        notification.append(new Error("First error"));
        notification.append(new Error("Second error"));
        notification.append(new Error("Third error"));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals("First error", exception.getErrors().get(0).message());
        assertEquals("Second error", exception.getErrors().get(1).message());
        assertEquals("Third error", exception.getErrors().get(2).message());
    }

    @Test
    void shouldHandleErrorsWithEmptyMessages() {
        // Given
        final var message = "Empty error messages";
        final var notification = Notification.create();
        notification.append(new Error(""));
        notification.append(new Error("Valid error"));
        notification.append(new Error(""));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals(3, exception.getErrors().size());
        assertEquals("", exception.getErrors().get(0).message());
        assertEquals("Valid error", exception.getErrors().get(1).message());
        assertEquals("", exception.getErrors().get(2).message());
    }

    @Test
    void shouldHandleErrorsWithSpecialCharacters() {
        // Given
        final var message = "Special characters in errors";
        final var notification = Notification.create();
        notification.append(new Error("Error with <tag>"));
        notification.append(new Error("Error with & ampersand"));
        notification.append(new Error("Error with \"quotes\""));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals(3, exception.getErrors().size());
        assertTrue(exception.getErrors().get(0).message().contains("<tag>"));
        assertTrue(exception.getErrors().get(1).message().contains("&"));
        assertTrue(exception.getErrors().get(2).message().contains("\""));
    }

    @Test
    void shouldCreateExceptionFromValidationFailure() {
        // Given
        final var message = "Validation failed for entity";
        final var notification = Notification.create();
        notification.append(new Error("Name cannot be null"));
        notification.append(new Error("Age must be greater than 0"));
        notification.append(new Error("Email must be valid"));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(3, exception.getErrors().size());
        assertTrue(exception.getErrors().stream()
                .anyMatch(e -> e.message().equals("Name cannot be null")));
        assertTrue(exception.getErrors().stream()
                .anyMatch(e -> e.message().equals("Age must be greater than 0")));
        assertTrue(exception.getErrors().stream()
                .anyMatch(e -> e.message().equals("Email must be valid")));
    }

    @Test
    void shouldHandleWhitespaceInMessage() {
        // Given
        final var message = "   Validation   failed   ";
        final var notification = Notification.create();
        notification.append(new Error("Error"));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldGetErrorsReturnsNonNullList() {
        // Given
        final var message = "Test";
        final var notification = Notification.create();

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertNotNull(exception.getErrors());
    }

    @Test
    void shouldHandleMixedErrorTypes() {
        // Given
        final var message = "Mixed validation errors";
        final var notification = Notification.create();
        notification.append(new Error("Required field error"));
        notification.append(new Error(""));
        notification.append(new Error("Format validation error"));
        notification.append(new Error("Business rule violation"));

        // When
        final var exception = new NotificationException(message, notification);

        // Then
        assertEquals(4, exception.getErrors().size());
        assertEquals("Required field error", exception.getErrors().get(0).message());
        assertEquals("", exception.getErrors().get(1).message());
        assertEquals("Format validation error", exception.getErrors().get(2).message());
        assertEquals("Business rule violation", exception.getErrors().get(3).message());
    }
}

