package soat.fastfood.backstage.application.domain.exceptions;

import org.junit.jupiter.api.Test;
import soat.fastfood.backstage.application.domain.validation.Error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

    @Test
    void shouldCreateDomainExceptionWithSingleError() {
        // Given
        final var error = new Error("Validation error");

        // When
        final var exception = DomainException.with(error);

        // Then
        assertNotNull(exception);
        assertEquals("Validation error", exception.getMessage());
        assertNotNull(exception.getErrors());
        assertEquals(1, exception.getErrors().size());
        assertEquals("Validation error", exception.getErrors().get(0).message());
    }

    @Test
    void shouldCreateDomainExceptionWithMultipleErrors() {
        // Given
        final var errors = List.of(
                new Error("Error 1"),
                new Error("Error 2"),
                new Error("Error 3")
        );

        // When
        final var exception = DomainException.with(errors);

        // Then
        assertNotNull(exception);
        assertEquals("", exception.getMessage()); // Mensagem vazia quando criado com lista
        assertNotNull(exception.getErrors());
        assertEquals(3, exception.getErrors().size());
        assertEquals("Error 1", exception.getErrors().get(0).message());
        assertEquals("Error 2", exception.getErrors().get(1).message());
        assertEquals("Error 3", exception.getErrors().get(2).message());
    }

    @Test
    void shouldCreateDomainExceptionWithEmptyList() {
        // Given
        final var errors = Collections.<Error>emptyList();

        // When
        final var exception = DomainException.with(errors);

        // Then
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNotNull(exception.getErrors());
        assertTrue(exception.getErrors().isEmpty());
    }

    @Test
    void shouldGetErrorsFromException() {
        // Given
        final var error = new Error("Test error");
        final var exception = DomainException.with(error);

        // When
        final var errors = exception.getErrors();

        // Then
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("Test error", errors.get(0).message());
    }

    @Test
    void shouldBeInstanceOfNoStacktraceException() {
        // Given
        final var error = new Error("Error message");

        // When
        final var exception = DomainException.with(error);

        // Then
        assertInstanceOf(NoStacktraceException.class, exception);
    }

    @Test
    void shouldCreateWithDifferentErrorMessages() {
        // Given
        final var error1 = new Error("Validation failed");
        final var error2 = new Error("Business rule violated");
        final var error3 = new Error("Required field missing");

        // When
        final var exception1 = DomainException.with(error1);
        final var exception2 = DomainException.with(error2);
        final var exception3 = DomainException.with(error3);

        // Then
        assertEquals("Validation failed", exception1.getMessage());
        assertEquals("Business rule violated", exception2.getMessage());
        assertEquals("Required field missing", exception3.getMessage());
    }

    @Test
    void shouldPreserveAllErrorsInList() {
        // Given
        final var errors = List.of(
                new Error("Error A"),
                new Error("Error B"),
                new Error("Error C"),
                new Error("Error D"),
                new Error("Error E")
        );

        // When
        final var exception = DomainException.with(errors);

        // Then
        assertEquals(5, exception.getErrors().size());
        for (int i = 0; i < errors.size(); i++) {
            assertEquals(errors.get(i).message(), exception.getErrors().get(i).message());
        }
    }

    @Test
    void shouldCreateWithListContainingSingleError() {
        // Given
        final var errors = List.of(new Error("Single error"));

        // When
        final var exception = DomainException.with(errors);

        // Then
        assertNotNull(exception);
        assertEquals(1, exception.getErrors().size());
        assertEquals("Single error", exception.getErrors().get(0).message());
    }

    @Test
    void shouldReturnEmptyMessageWhenCreatedWithList() {
        // Given
        final var errors = List.of(new Error("Some error"));

        // When
        final var exception = DomainException.with(errors);

        // Then
        assertEquals("", exception.getMessage());
    }

    @Test
    void shouldReturnErrorMessageWhenCreatedWithSingleError() {
        // Given
        final var errorMessage = "This is the error message";
        final var error = new Error(errorMessage);

        // When
        final var exception = DomainException.with(error);

        // Then
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldHandleNullError() {
        // Given
        Error nullError = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> DomainException.with(nullError));
    }

    @Test
    void shouldHandleErrorWithEmptyMessage() {
        // Given
        final var error = new Error("");

        // When
        final var exception = DomainException.with(error);

        // Then
        assertEquals("", exception.getMessage());
        assertEquals(1, exception.getErrors().size());
        assertEquals("", exception.getErrors().get(0).message());
    }

    @Test
    void shouldHandleErrorWithWhitespaceMessage() {
        // Given
        final var error = new Error("   ");

        // When
        final var exception = DomainException.with(error);

        // Then
        assertEquals("   ", exception.getMessage());
        assertEquals(1, exception.getErrors().size());
    }

    @Test
    void shouldHandleListWithMultipleIdenticalErrors() {
        // Given
        final var errorMessage = "Duplicate error";
        final var errors = List.of(
                new Error(errorMessage),
                new Error(errorMessage),
                new Error(errorMessage)
        );

        // When
        final var exception = DomainException.with(errors);

        // Then
        assertEquals(3, exception.getErrors().size());
        exception.getErrors().forEach(error ->
                assertEquals(errorMessage, error.message()));
    }

    @Test
    void shouldCreateExceptionWithLongErrorMessage() {
        // Given
        final var longMessage = "This is a very long error message that contains a lot of text " +
                "to test if the exception can handle long messages properly without any issues " +
                "and maintain the integrity of the data throughout the process.";
        final var error = new Error(longMessage);

        // When
        final var exception = DomainException.with(error);

        // Then
        assertEquals(longMessage, exception.getMessage());
        assertEquals(longMessage, exception.getErrors().get(0).message());
    }

    @Test
    void shouldCreateExceptionWithSpecialCharactersInMessage() {
        // Given
        final var specialMessage = "Error: @#$%^&*()_+-=[]{}|;':\",./<>?";
        final var error = new Error(specialMessage);

        // When
        final var exception = DomainException.with(error);

        // Then
        assertEquals(specialMessage, exception.getMessage());
        assertEquals(specialMessage, exception.getErrors().get(0).message());
    }

    @Test
    void shouldAllowMultipleExceptionsWithDifferentErrors() {
        // Given
        final var error1 = new Error("First error");
        final var error2 = new Error("Second error");
        final var errors = List.of(new Error("Third error"), new Error("Fourth error"));

        // When
        final var exception1 = DomainException.with(error1);
        final var exception2 = DomainException.with(error2);
        final var exception3 = DomainException.with(errors);

        // Then
        assertEquals(1, exception1.getErrors().size());
        assertEquals(1, exception2.getErrors().size());
        assertEquals(2, exception3.getErrors().size());
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    void shouldCreateExceptionWithMixedErrorTypes() {
        // Given
        final var errors = List.of(
                new Error("Validation error"),
                new Error("Business rule error"),
                new Error(""),
                new Error("Another error")
        );

        // When
        final var exception = DomainException.with(errors);

        // Then
        assertEquals(4, exception.getErrors().size());
        assertEquals("Validation error", exception.getErrors().get(0).message());
        assertEquals("", exception.getErrors().get(2).message());
    }

    @Test
    void shouldReturnNonNullErrorsList() {
        // Given
        final var error = new Error("Test");
        final var exception = DomainException.with(error);

        // When
        final var errors = exception.getErrors();

        // Then
        assertNotNull(errors);
    }
}

