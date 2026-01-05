package soat.fastfood.backstage.adapter.inbound.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import soat.fastfood.backstage.adapter.inbound.api.dto.ErrorDto;
import soat.fastfood.backstage.application.domain.exceptions.DomainException;
import soat.fastfood.backstage.application.domain.exceptions.NotFoundException;
import soat.fastfood.backstage.application.domain.utils.InstantUtils;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(final NotFoundException ex) {
        log.error("NotFoundException: ", ex);
        final var error = new ErrorDto(
                InstantUtils.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getErrors()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(value = DomainException.class)
    public ResponseEntity<ErrorDto> handleDomainException(final DomainException ex) {
        log.error("DomainException: ", ex);
        final var error = new ErrorDto(
                InstantUtils.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getErrors()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorDto> handleException(final Exception ex) {
        log.error("Exception: ", ex);
        final var error = new ErrorDto(
                InstantUtils.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(final RuntimeException ex) {
        log.error("RunTimeException: ", ex);
        final var error = new ErrorDto(
                InstantUtils.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
