package soat.fastfood.backstage.application.domain.exceptions;

import soat.fastfood.backstage.application.domain.AggregateRoot;
import soat.fastfood.backstage.application.domain.Identifier;
import soat.fastfood.backstage.application.domain.validation.Error;

import java.util.Collections;
import java.util.List;

public class NotFoundException extends DomainException {
    protected NotFoundException(final String aMessage, final List<Error> someErrors) {
        super(aMessage, someErrors);
    }

    public static NotFoundException with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final Identifier id
    ) {
        final var anError = "%s with id %s was not found".formatted(
                anAggregate.getSimpleName().toLowerCase(),
                id.getValue()
        );
        return new NotFoundException(anError, Collections.emptyList());
    }

    public static NotFoundException with(final Error anError) {
        return new NotFoundException(anError.message(), List.of(anError));
    }

}
