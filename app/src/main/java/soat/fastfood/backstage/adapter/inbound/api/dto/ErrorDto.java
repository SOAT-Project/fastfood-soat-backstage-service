package soat.fastfood.backstage.adapter.inbound.api.dto;

import soat.fastfood.backstage.application.domain.validation.Error;

import java.time.Instant;
import java.util.List;

public record ErrorDto(
        Instant timestamp,
        Integer status,
        List<Error> errors
) {
}
