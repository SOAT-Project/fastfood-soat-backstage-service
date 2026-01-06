package soat.fastfood.backstage.application.domain.exceptions;

import soat.fastfood.backstage.application.domain.validation.Error;

import java.util.List;

public class DomainException extends NoStacktraceException {
    protected final List<Error> errors;

    protected DomainException(final String aMessage, final List<Error> someErrors) {
        super(aMessage);
        this.errors = someErrors;
    }

    public static DomainException with(final List<Error> someErrors) {
        return new DomainException("", someErrors);
    }

    public static DomainException with(final Error anError) {
        return new DomainException(anError.message(), List.of(anError));
    }

    public List<Error> getErrors() {
        return errors;
    }
}
