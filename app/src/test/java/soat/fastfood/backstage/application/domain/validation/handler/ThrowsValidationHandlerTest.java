
package soat.fastfood.backstage.application.domain.validation.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat.fastfood.backstage.application.domain.exceptions.DomainException;
import soat.fastfood.backstage.application.domain.validation.Error;
import soat.fastfood.backstage.application.domain.validation.ValidationHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThrowsValidationHandlerTest {

    private ThrowsValidationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ThrowsValidationHandler();
    }

    @Test
    void shouldThrowDomainExceptionWhenAppendingError() {
        // Given
        final var error = new Error("Validation error");

        // When & Then
        final var exception = assertThrows(DomainException.class, () -> handler.append(error));
        
        assertNotNull(exception);
        assertEquals(1, exception.getErrors().size());
        assertEquals("Validation error", exception.getErrors().get(0).message());
    }

    @Test
    void shouldThrowDomainExceptionWhenAppendingHandler() {
        // Given
        final var errors = List.of(
                new Error("Error 1"),
                new Error("Error 2")
        );
        final var otherHandler = new ValidationHandler() {
            @Override
            public ValidationHandler append(Error anError) {
                return this;
            }

            @Override
            public ValidationHandler append(ValidationHandler aHandler) {
                return this;
            }

            @Override
            public <T> T validate(Validation<T> aValidation) {
                return null;
            }

            @Override
            public List<Error> getErrors() {
                return errors;
            }
        };

        // When & Then
        final var exception = assertThrows(DomainException.class, 
                () -> handler.append(otherHandler));
        
        assertNotNull(exception);
        assertEquals(2, exception.getErrors().size());
    }

    @Test
    void shouldValidateSuccessfully() {
        // Given
        final ValidationHandler.Validation<String> validation = () -> "Success";

        // When
        final var result = handler.validate(validation);

        // Then
        assertEquals("Success", result);
    }

    @Test
    void shouldThrowDomainExceptionWhenValidationFails() {
        // Given
        final ValidationHandler.Validation<String> validation = () -> {
            throw new RuntimeException("Validation failed");
        };

        // When & Then
        final var exception = assertThrows(DomainException.class, 
                () -> handler.validate(validation));
        
        assertNotNull(exception);
        assertEquals(1, exception.getErrors().size());
        assertEquals("Validation failed", exception.getErrors().get(0).message());
    }

    @Test
    void shouldReturnNullForGetErrors() {
        // When
        final var errors = handler.getErrors();

        // Then
        assertNull(errors);
    }

    @Test
    void shouldValidateWithDifferentReturnTypes() {
        // Given
        final ValidationHandler.Validation<Integer> intValidation = () -> 42;
        final ValidationHandler.Validation<Boolean> boolValidation = () -> true;
        final ValidationHandler.Validation<Object> objectValidation = () -> new Object();

        // When
        final var intResult = handler.validate(intValidation);
        final var boolResult = handler.validate(boolValidation);
        final var objectResult = handler.validate(objectValidation);

        // Then
        assertEquals(42, intResult);
        assertTrue(boolResult);
        assertNotNull(objectResult);
    }

    @Test
    void shouldThrowDomainExceptionWithCorrectErrorMessage() {
        // Given
        final var errorMessage = "Custom validation error";
        final var error = new Error(errorMessage);

        // When & Then
        final var exception = assertThrows(DomainException.class, 
                () -> handler.append(error));
        
        assertTrue(exception.getErrors().stream()
                .anyMatch(e -> e.message().equals(errorMessage)));
    }

    @Test
    void shouldHandleNullPointerExceptionInValidation() {
        // Given
        final ValidationHandler.Validation<String> validation = () -> {
            throw new NullPointerException("Null value");
        };

        // When & Then
        final var exception = assertThrows(DomainException.class, 
                () -> handler.validate(validation));
        
        assertEquals("Null value", exception.getErrors().get(0).message());
    }

    @Test
    void shouldHandleIllegalArgumentExceptionInValidation() {
        // Given
        final ValidationHandler.Validation<String> validation = () -> {
            throw new IllegalArgumentException("Invalid argument");
        };

        // When & Then
        final var exception = assertThrows(DomainException.class, 
                () -> handler.validate(validation));
        
        assertEquals("Invalid argument", exception.getErrors().get(0).message());
    }

    @Test
    void shouldThrowExceptionImmediatelyOnAppend() {
        // Given
        final var error = new Error("Immediate error");

        // When & Then
        assertThrows(DomainException.class, () -> {
            handler.append(error);
            fail("Should have thrown DomainException immediately");
        });
    }

    @Test
    void shouldValidateAndReturnNull() {
        // Given
        final ValidationHandler.Validation<String> validation = () -> null;

        // When
        final var result = handler.validate(validation);

        // Then
        assertNull(result);
    }

    @Test
    void shouldAppendErrorWithEmptyMessage() {
        // Given
        final var error = new Error("");

        // When & Then
        final var exception = assertThrows(DomainException.class, 
                () -> handler.append(error));
        
        assertEquals("", exception.getErrors().get(0).message());
    }

    @Test
    void shouldAppendHandlerWithEmptyErrorsList() {
        // Given
        final var otherHandler = new ValidationHandler() {
            @Override
            public ValidationHandler append(Error anError) {
                return this;
            }

            @Override
            public ValidationHandler append(ValidationHandler aHandler) {
                return this;
            }

            @Override
            public <T> T validate(Validation<T> aValidation) {
                return null;
            }

            @Override
            public List<Error> getErrors() {
                return List.of();
            }
        };

        // When & Then
        final var exception = assertThrows(DomainException.class, 
                () -> handler.append(otherHandler));
        
        assertTrue(exception.getErrors().isEmpty());
    }

    @Test
    void shouldHandleMultipleErrorsFromHandler() {
        // Given
        final var errors = List.of(
                new Error("Error 1"),
                new Error("Error 2"),
                new Error("Error 3"),
                new Error("Error 4")
        );
        final var otherHandler = new ValidationHandler() {
            @Override
            public ValidationHandler append(Error anError) {
                return this;
            }

            @Override
            public ValidationHandler append(ValidationHandler aHandler) {
                return this;
            }

            @Override
            public <T> T validate(Validation<T> aValidation) {
                return null;
            }

            @Override
            public List<Error> getErrors() {
                return errors;
            }
        };

        // When & Then
        final var exception = assertThrows(DomainException.class, 
                () -> handler.append(otherHandler));
        
        assertEquals(4, exception.getErrors().size());
    }

    @Test
    void shouldValidateWithComplexObject() {
        // Given
        final var complexObject = new Object() {
            private final String field1 = "value1";
            private final int field2 = 123;
        };
        final ValidationHandler.Validation<Object> validation = () -> complexObject;

        // When
        final var result = handler.validate(validation);

        // Then
        assertNotNull(result);
        assertEquals(complexObject, result);
    }

    @Test
    void shouldHandleExceptionWithNullMessage() {
        // Given
        final ValidationHandler.Validation<String> validation = () -> {
            throw new RuntimeException((String) null);
        };

        // When & Then
        final var exception = assertThrows(DomainException.class, 
                () -> handler.validate(validation));
        
        assertNotNull(exception.getErrors());
        assertEquals(1, exception.getErrors().size());
    }

    @Test
    void shouldValidateWithEmptyString() {
        // Given
        final ValidationHandler.Validation<String> validation = () -> "";

        // When
        final var result = handler.validate(validation);

        // Then
        assertEquals("", result);
    }

}

