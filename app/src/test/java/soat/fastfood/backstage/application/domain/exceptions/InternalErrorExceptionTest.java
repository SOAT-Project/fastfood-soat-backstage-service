package soat.fastfood.backstage.application.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InternalErrorExceptionTest {

    @Test
    void shouldCreateInternalErrorExceptionWithMessageAndThrowable() {
        // Given
        final var message = "Internal error occurred";
        final var cause = new RuntimeException("Root cause");

        // When
        final var exception = InternalErrorException.with(message, cause);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateInternalErrorExceptionWithNullMessage() {
        // Given
        final var cause = new RuntimeException("Root cause");

        // When
        final var exception = InternalErrorException.with(null, cause);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateInternalErrorExceptionWithNullCause() {
        // Given
        final var message = "Internal error occurred";

        // When
        final var exception = InternalErrorException.with(message, null);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateInternalErrorExceptionWithBothNull() {
        // When
        final var exception = InternalErrorException.with(null, null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldBeInstanceOfNoStacktraceException() {
        // Given
        final var message = "Internal error";
        final var cause = new Exception("Cause");

        // When
        final var exception = InternalErrorException.with(message, cause);

        // Then
        assertInstanceOf(NoStacktraceException.class, exception);
    }

    @Test
    void shouldCreateWithDifferentExceptionTypes() {
        // Given
        final var runtimeException = new RuntimeException("Runtime error");
        final var illegalArgumentException = new IllegalArgumentException("Illegal argument");
        final var nullPointerException = new NullPointerException("Null pointer");

        // When
        final var exception1 = InternalErrorException.with("Error 1", runtimeException);
        final var exception2 = InternalErrorException.with("Error 2", illegalArgumentException);
        final var exception3 = InternalErrorException.with("Error 3", nullPointerException);

        // Then
        assertEquals(runtimeException, exception1.getCause());
        assertEquals(illegalArgumentException, exception2.getCause());
        assertEquals(nullPointerException, exception3.getCause());
    }

    @Test
    void shouldPreserveOriginalExceptionMessage() {
        // Given
        final var originalMessage = "Original exception message";
        final var cause = new IllegalStateException(originalMessage);
        final var wrapperMessage = "Wrapper message";

        // When
        final var exception = InternalErrorException.with(wrapperMessage, cause);

        // Then
        assertEquals(wrapperMessage, exception.getMessage());
        assertEquals(originalMessage, exception.getCause().getMessage());
    }

    @Test
    void shouldCreateWithEmptyMessage() {
        // Given
        final var message = "";
        final var cause = new RuntimeException("Cause");

        // When
        final var exception = InternalErrorException.with(message, cause);

        // Then
        assertEquals("", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void shouldCreateWithLongErrorMessage() {
        // Given
        final var longMessage = "This is a very long error message that contains a lot of text " +
                "to test if the exception can handle long messages properly without any issues " +
                "and maintain the integrity of the data throughout the process.";
        final var cause = new RuntimeException("Cause");

        // When
        final var exception = InternalErrorException.with(longMessage, cause);

        // Then
        assertEquals(longMessage, exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void shouldCreateWithSpecialCharactersInMessage() {
        // Given
        final var specialMessage = "Error: @#$%^&*()_+-=[]{}|;':\",./<>?";
        final var cause = new RuntimeException("Cause");

        // When
        final var exception = InternalErrorException.with(specialMessage, cause);

        // Then
        assertEquals(specialMessage, exception.getMessage());
    }

    @Test
    void shouldHandleNestedExceptions() {
        // Given
        final var rootCause = new IllegalArgumentException("Root cause");
        final var middleCause = new RuntimeException("Middle cause", rootCause);
        final var message = "Top level error";

        // When
        final var exception = InternalErrorException.with(message, middleCause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(middleCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    void shouldCreateMultipleIndependentExceptions() {
        // Given & When
        final var exception1 = InternalErrorException.with("Error 1", new RuntimeException());
        final var exception2 = InternalErrorException.with("Error 2", new RuntimeException());
        final var exception3 = InternalErrorException.with("Error 3", new RuntimeException());

        // Then
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
        assertNotEquals(exception2.getMessage(), exception3.getMessage());
        assertNotEquals(exception1.getCause(), exception2.getCause());
    }
}

