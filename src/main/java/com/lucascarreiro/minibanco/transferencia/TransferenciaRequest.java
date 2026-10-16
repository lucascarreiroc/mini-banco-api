package com.lucascarreiro.minibanco.transferencia;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferenciaRequest(
        @NotNull Long contaOrigemId,
        @NotNull Long contaDestinoId,
        @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal valor
) {
}